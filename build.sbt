name := "scalatest"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= {
  val akkaV       = "2.3.11"
  val akkaStreamV = "1.0"
  val scalaTestV  = "2.2.5"
  Seq(
    "org.scalatest"           %  "scalatest_2.11"                 % "2.2.4" % "test",
    "org.twitter4j"           %  "twitter4j-stream"               % "4.0.4",
    "com.typesafe"            %  "config"                         % "1.3.0",
    "org.scalaj"              %% "scalaj-http"                    % "1.1.5",
    "org.scala-lang.modules"  %% "scala-parser-combinators"       % "1.0.4",
    "org.scalatest"           %% "scalatest"                      % scalaTestV % "test",
    "com.typesafe.akka" %% "akka-actor"                           % akkaV,
    "com.typesafe.akka" %% "akka-stream-experimental"             % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-core-experimental"          % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-experimental"               % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-spray-json-experimental"    % akkaStreamV,
    "com.typesafe.akka" %% "akka-http-testkit-experimental"       % akkaStreamV
  )
}


