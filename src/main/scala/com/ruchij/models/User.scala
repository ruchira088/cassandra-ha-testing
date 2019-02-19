package com.ruchij.models
import java.util.UUID

import org.joda.time.DateTime

case class User(
  id: UUID,
  createdAt: DateTime,
  firstName: String,
  lastName: String,
  age: Int,
  email: String,
  isMarried: Boolean
)
