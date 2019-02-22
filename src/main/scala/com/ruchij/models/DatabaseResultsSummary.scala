package com.ruchij.models
import play.api.libs.json.{Json, OWrites, Writes}

case class DatabaseResultsSummary[InsertionResult](
  insertionResult: InsertionResult,
  count: Long,
  userByEmail: Option[User],
  userById: Option[User],
  usersByAgeCount: Int,
  usersByFirstNameCount: Int
)

object DatabaseResultsSummary {
  implicit def databaseResultsSummaryWrites[InsertionResult: Writes]: OWrites[DatabaseResultsSummary[InsertionResult]] =
    Json.writes[DatabaseResultsSummary[InsertionResult]]
}