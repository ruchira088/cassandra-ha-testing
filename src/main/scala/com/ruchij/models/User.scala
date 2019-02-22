package com.ruchij.models
import java.util.UUID

import com.ruchij.json.JsonWrites.dateTimeWrites
import org.joda.time.DateTime
import play.api.libs.json.{Json, OWrites}

case class User(
  id: UUID,
  createdAt: DateTime,
  firstName: String,
  lastName: String,
  age: Int,
  email: String,
  isMarried: Boolean
)

object User {
  implicit val userWrites: OWrites[User] = Json.writes[User]
}