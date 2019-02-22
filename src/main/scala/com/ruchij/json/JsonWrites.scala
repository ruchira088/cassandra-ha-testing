package com.ruchij.json
import org.joda.time.DateTime
import play.api.libs.json.{JsString, Writes}

object JsonWrites {
  implicit val dateTimeWrites: Writes[DateTime] =
    (dateTime: DateTime) => JsString(dateTime.toString)
}
