package com.ruchij.dao

import java.util.UUID

import com.ruchij.FutureOpt
import com.ruchij.dao.db.slick.MappedColumnTypes.dateTimeMappedColumnType
import com.ruchij.exceptions.DuplicatedEntryException
import com.ruchij.models.User
import org.joda.time.DateTime
import scalaz.{OptionT, ReaderT}
import slick.basic.BasicBackend
import slick.jdbc.JdbcProfile
import slick.lifted.ProvenShape

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SlickUserTable(val jdbcProfile: JdbcProfile) extends UserDao[BasicBackend#DatabaseDef] {
  import jdbcProfile.api._

  class Users(tag: Tag) extends Table[User](tag, "users") {

    def id: Rep[UUID] = column[UUID]("id", O.PrimaryKey)
    def createdAt: Rep[DateTime] = column[DateTime]("created_at")
    def firstName: Rep[String] = column[String]("first_name")
    def lastName: Rep[String] = column[String]("last_name")
    def age: Rep[Int] = column[Int]("age")
    def email: Rep[String] = column[String]("email")
    def isMarried: Rep[Boolean] = column[Boolean]("is_married")

    override def * : ProvenShape[User] =
      (id, createdAt, firstName, lastName, age, email, isMarried) <> (User.apply _ tupled, User.unapply)
  }

  val users: TableQuery[Users] = TableQuery[Users]

  override type InsertionResult = Int

  override type InitializationResult = Unit

  override def init(
    implicit executionContext: ExecutionContext
  ): ReaderT[Future, BasicBackend#DatabaseDef, InitializationResult] =
    ReaderT { database =>
      database.run(users.schema.createIfNotExists)
    }

  override def insert(
    user: User
  )(implicit executionContext: ExecutionContext): ReaderT[Future, BasicBackend#DatabaseDef, InsertionResult] =
    ReaderT { _.run(users += user) }

  override def getByEmail(
    email: String
  )(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, BasicBackend#DatabaseDef, User] =
    fetchUser { _.email === email }

  override def getById(
    id: UUID
  )(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, BasicBackend#DatabaseDef, User] =
    fetchUser { _.id === id }

  override def getByAge(
    age: Int
  )(implicit executionContext: ExecutionContext): ReaderT[Future, BasicBackend#DatabaseDef, List[User]] =
    ReaderT {
      _.run(users.filter(_.age === age).result).map(_.toList)
    }

  override def getByFirstName(
    firstName: String
  )(implicit executionContext: ExecutionContext): ReaderT[Future, BasicBackend#DatabaseDef, List[User]] =
    ReaderT {
      _.run(users.filter(_.firstName === firstName).result).map(_.toList)
    }

  private def fetchUser(
    selector: Users => Rep[Boolean]
  )(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, BasicBackend#DatabaseDef, User] =
    ReaderT[FutureOpt, BasicBackend#DatabaseDef, User] { database =>
      OptionT {
        database
          .run(users.filter(selector).result)
          .map(_.toList)
          .flatMap {
            case Nil => Future.successful(None)
            case user :: Nil => Future.successful(Some(user))
            case allResults =>
              Future.failed(DuplicatedEntryException(allResults.toList))
          }
      }
    }
}
