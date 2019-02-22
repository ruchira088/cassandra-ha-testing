package com.ruchij
import java.util.concurrent.atomic.AtomicLong

import akka.actor.ActorSystem
import ch.qos.logback.classic
import ch.qos.logback.classic.Level
import com.eed3si9n.ruchij.BuildInfo
import com.ruchij.dao.SlickUserTable
import com.ruchij.models.{DatabaseResultsSummary, Random, User}
import com.typesafe.scalalogging
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json
import scalaz.ReaderT
import scalaz.std.scalaFuture.futureInstance
import slick.basic.BasicBackend
import slick.jdbc.PostgresProfile
import slick.jdbc.PostgresProfile.api.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object App {
  private val appLogger = scalalogging.Logger[App]

  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem(BuildInfo.name)

    val rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME)

    val count: AtomicLong = new AtomicLong(0L)

    Option(rootLogger).foreach {
      case logger: classic.Logger =>
        logger.setLevel(Level.INFO)

      case logger => logger.error("Not Logback logger")
    }

//    val cassandraAsyncContext: AsyncCassandraContext =
//      Context.cassandraAsync(ConfigFactory.load().getConfig("cassandra"))
//
//    val userDao: QuillUserDao.type = QuillUserDao

    val databaseConfig: PostgresProfile.backend.Database = Database.forConfig("postgres")
    val userDao = new SlickUserTable(PostgresProfile)

    actorSystem.scheduler.schedule(2 seconds, 10 milliseconds) {
      val user = Random.user()

      val result =
        for {
          _ <- userDao.init
          insertionResult <- userDao.insert(user)

//          _ <- ReaderT[Future, BasicBackend#DatabaseDef, Unit] { _ =>
//            delay(1 second)
//          }

          userByEmail <- userDao.getByEmail(user.email).mapT[Future, List[User]](_.run.map(_.toList))
          userById <- userDao.getById(user.id).mapT[Future, List[User]](_.run.map(_.toList))

          usersByAge <- userDao.getByAge(user.age)
          usersByFirstName <- userDao.getByFirstName(user.firstName)
        } yield
          DatabaseResultsSummary(insertionResult, count.incrementAndGet(), userByEmail.headOption, userById.headOption, usersByAge.length, usersByFirstName.length)

      result(databaseConfig).onComplete {
        case Success(summary) =>
          appLogger.info(Json.prettyPrint(Json.toJson(summary)))

        case Failure(throwable) =>
          appLogger.error(throwable.getMessage, throwable)
      }
    }

    println("Database HA testing started.")
  }

  def delay(duration: FiniteDuration)(implicit actorSystem: ActorSystem): Future[Unit] = {
    val promise = Promise[Unit]

    actorSystem.scheduler.scheduleOnce(duration) {
      promise.success((): Unit)
    }

    promise.future
  }
}
