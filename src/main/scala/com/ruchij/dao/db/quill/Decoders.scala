package com.ruchij.dao.db.quill
import java.util.Date

import io.getquill.MappedEncoding
import org.joda.time.DateTime

object Decoders {
  implicit val dateTimeDecoder: MappedEncoding[Date, DateTime] =
    MappedEncoding[Date, DateTime] { date => new DateTime(date.getTime) }
}