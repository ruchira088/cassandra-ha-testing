package com.ruchij.models

case class DatabaseResultsSummary[InsertionResult](
  insertionResult: InsertionResult,
  userByEmail: Option[User],
  userById: Option[User],
  usersByAge: List[User],
  usersByFirstName: List[User]
)
