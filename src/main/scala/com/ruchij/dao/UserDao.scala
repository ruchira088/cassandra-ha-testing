package com.ruchij.dao
import java.util.UUID

import com.ruchij.FutureOpt
import com.ruchij.models.User
import scalaz.ReaderT

import scala.concurrent.{ExecutionContext, Future}
import scala.language.reflectiveCalls

trait UserDao[A] {
  type InsertionResult

  def insert(user: User)(implicit executionContext: ExecutionContext): ReaderT[Future, A, InsertionResult]

  def getByEmail(email: String)(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, A, User]

  def getById(id: UUID)(implicit executionContext: ExecutionContext): ReaderT[FutureOpt, A, User]

  def getByAge(age: Int)(implicit executionContext: ExecutionContext): ReaderT[Future, A, List[User]]

  def getByFirstName(firstName: String)(implicit executionContext: ExecutionContext): ReaderT[Future, A, List[User]]
}
