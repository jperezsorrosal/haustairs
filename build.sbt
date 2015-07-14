name := "scalatest"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++=Seq(
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  "com.typesafe" % "config" % "1.3.0",
  "org.scalaj" %% "scalaj-http" % "1.1.5",
  "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
)

