package com.ruchij.dao.db.quill
import com.typesafe.config.{Config, ConfigValueFactory}
import io.getquill.{CassandraAsyncContext, SnakeCase}

import scala.collection.JavaConverters._
import scala.util.Try

object Context {
  private val CONTACT_POINTS_PATH = "session.contactPoints"

  def cassandraAsync(config: Config): CassandraAsyncContext[SnakeCase] =
    new CassandraAsyncContext[SnakeCase](
      SnakeCase,
      config.withValue(
        CONTACT_POINTS_PATH,
        ConfigValueFactory.fromIterable {
          Try(config.getString(CONTACT_POINTS_PATH))
            .map(_.split(",").toList.map(_.trim))
            .getOrElse(List.empty[String])
            .asJava
        }
      )
    )
}
