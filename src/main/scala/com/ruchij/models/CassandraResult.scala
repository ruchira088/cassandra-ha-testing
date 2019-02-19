package com.ruchij.models

case class CassandraResult[InsertionResult](
  insertionResult: InsertionResult,
  userByEmail: Option[User],
  userById: Option[User],
  usersByAge: List[User],
  usersByFirstName: List[User]
)
