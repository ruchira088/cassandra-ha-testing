import sbt._

object Dependencies
{
  val SCALA_VERSION = "2.12.8"

  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.5.21"

  lazy val javaFaker = "com.github.javafaker" % "javafaker" % "0.17.2"

  lazy val jodaTime = "joda-time" % "joda-time" % "2.10.1"

  lazy val quillCassandra = "io.getquill" %% "quill-cassandra" % "3.0.1"

  lazy val scalazCore = "org.scalaz" %% "scalaz-core" % "7.2.27"

  lazy val typesafeConfig = "com.typesafe" % "config" % "1.3.2"

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"

  lazy val pegdown = "org.pegdown" % "pegdown" % "1.6.0"
}
