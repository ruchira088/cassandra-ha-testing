import Dependencies._

lazy val root =
  (project in file("."))
    .enablePlugins(BuildInfoPlugin)
    .settings(
      name := "cassandra-project",
      organization := "com.ruchij",
      scalaVersion := SCALA_VERSION,
      libraryDependencies ++= rootDependencies ++ rootTestDependencies,
      buildInfoKeys := BuildInfoKey.ofN(name, organization, version, scalaVersion, sbtVersion),
      buildInfoPackage := "com.eed3si9n.ruchij",
      assemblyJarName in assembly := "cassandra-project-assembly.jar",
      assemblyMergeStrategy in assembly := {
        case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
        case _ => MergeStrategy.first
      },
      testOptions in Test +=
        Tests.Argument(TestFrameworks.ScalaTest, "-h", "target/test-results")
    )

lazy val rootDependencies =
  Seq(akkaActor, javaFaker, jodaTime, quillCassandra, scalazCore)

lazy val rootTestDependencies =
  Seq(scalaTest, pegdown)

addCommandAlias("testWithCoverage", "; clean; coverage; test; coverageReport")
