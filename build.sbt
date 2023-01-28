ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.2.2"

val AkkaVersion = "2.7.0"

lazy val root = (project in file("."))
  .settings(
    name := "term_work",
    libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.13",
    libraryDependencies += "org.scalatest" %% "scalatest" % "latest.integration" % "test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-parser-combinators" % "2.1.1"
  )
