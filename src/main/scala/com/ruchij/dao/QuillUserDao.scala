package com.ruchij.dao

import java.util.UUID

import com.ruchij.exceptions.DuplicatedEntryException
import com.ruchij.models.User
import com.ruchij.quill.Decoders.dateTimeDecoder
import com.ruchij.quill.Encoders.dateTimeEncoder
import com.ruchij.{AsyncCassandraContext, FutureOpt}
import scalaz.{OptionT, ReaderT}

import scala.concurrent.{ExecutionContext, Future}
import scala.language.higherKinds

object QuillUserDao extends UserDao[AsyncCassandraContext] {

  override type InsertionResult = AsyncCassandraContext#RunActionResult

  override def insert(
    user: User
  )(implicit executionContext: ExecutionContext): ReaderT[Future, AsyncCassandraContext, InsertionResult] =
    ReaderT { asyncCassandraContext =>
      import asyncCassandraContext._

      run {
        quote { querySchema[User]("user").insert(lift(user)) }
      }
    }

  override def getByEmail(
    email: String
  )(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, AsyncCassandraContext, User] =
    ReaderT[FutureOpt, AsyncCassandraContext, User] { asyncCassandraContext =>
      import asyncCassandraContext._

      fetchUser(asyncCassandraContext) {
        quote {
          querySchema[User]("user_by_email").filter(_.email == lift(email)).take(2)
        }
      }
    }

  override def getById(
    id: UUID
  )(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, AsyncCassandraContext, User] =
    ReaderT[FutureOpt, AsyncCassandraContext, User] { asyncCassandraContext =>
      import asyncCassandraContext._

      fetchUser(asyncCassandraContext) {
        quote {
          querySchema[User]("user").filter(_.id == lift(id)).take(2)
        }
      }
    }

  override def getByAge(
    age: Int
  )(implicit executionContext: ExecutionContext): ReaderT[Future, AsyncCassandraContext, List[User]] =
    ReaderT[Future, AsyncCassandraContext, List[User]] { asyncCassandraContext =>
      import asyncCassandraContext._

      run {
        quote {
          querySchema[User]("user_by_age").filter(_.age == lift(age))
        }
      }
    }

  override def getByFirstName(
    firstName: String
  )(implicit executionContext: ExecutionContext): ReaderT[Future, AsyncCassandraContext, List[User]] =
    ReaderT[Future, AsyncCassandraContext, List[User]] { asyncCassandraContext =>
      import asyncCassandraContext._

      run {
        quote {
          querySchema[User]("user_by_first_name").filter(_.firstName == lift(firstName))
        }
      }
    }

  private def fetchUser(asyncCassandraContext: AsyncCassandraContext)(
    quotedQuery: asyncCassandraContext.Quoted[asyncCassandraContext.Query[User]]
  )(implicit executionContext: ExecutionContext): FutureOpt[User] =
    OptionT[Future, User] {
      import asyncCassandraContext._

      run(quotedQuery)
        .flatMap {
          case users if users.length > 1 => Future.failed(DuplicatedEntryException(users))
          case userOpt => Future.successful(userOpt.headOption)
        }
    }

}
