package com.ruchij
import akka.actor.ActorSystem
import com.eed3si9n.ruchij.BuildInfo
import com.ruchij.dao.QuillUserDao
import com.ruchij.models.{CassandraResult, Random, User}
import com.ruchij.quill.Context
import com.typesafe.config.ConfigFactory
import scalaz.ReaderT
import scalaz.std.scalaFuture.futureInstance

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Future, Promise}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object App {
  def main(args: Array[String]): Unit = {
    implicit val actorSystem: ActorSystem = ActorSystem(BuildInfo.name)

    val cassandraAsyncContext: AsyncCassandraContext =
      Context.cassandraAsync(ConfigFactory.load().getConfig("cassandra"))

    val userDao: QuillUserDao.type = QuillUserDao

    actorSystem.scheduler.schedule(2 seconds, 3 seconds) {
      val user = Random.user()

      val result: ReaderT[Future, AsyncCassandraContext, CassandraResult[_]] =
        for {
          insertionResult <- userDao.insert(user)

          _ <- ReaderT[Future, AsyncCassandraContext, Unit] { _ =>
            delay(1 second)
          }

          userByEmail <- userDao.getByEmail(user.email).mapT[Future, List[User]](_.run.map(_.toList))
          userById <- userDao.getById(user.id).mapT[Future, List[User]](_.run.map(_.toList))

          usersByAge <- userDao.getByAge(user.age)
          usersByFirstName <- userDao.getByFirstName(user.firstName)
        } yield
          CassandraResult(insertionResult, userByEmail.headOption, userById.headOption, usersByAge, usersByFirstName)

      result(cassandraAsyncContext).onComplete {
        case Success(users) => println(users)
        case Failure(throwable) => System.err.println(throwable)
      }
    }

    println("Cassandra HA testing started.")
  }

  def delay(duration: FiniteDuration)(implicit actorSystem: ActorSystem): Future[Unit] = {
    val promise = Promise[Unit]

    actorSystem.scheduler.scheduleOnce(duration) {
      promise.success((): Unit)
    }

    promise.future
  }
}
