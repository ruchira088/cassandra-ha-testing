package com.ruchij.dao.db.quill
import java.util.Date

import io.getquill.MappedEncoding
import org.joda.time.DateTime

object Encoders {
  implicit val dateTimeEncoder: MappedEncoding[DateTime, Date] =
    MappedEncoding[DateTime, Date] { dateTime => new Date(dateTime.getMillis) }
}
