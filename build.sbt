//********************************************************
// Play settings
//*************

name := """gosantix"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.twitter"              % "finagle-core_2.11"       % "6.24.0",
  "com.twitter"              % "finagle-stream_2.11"     % "6.24.0",
  "com.twitter"              % "finagle-http_2.11"       % "6.24.0",
  "org.twitter4j"            % "twitter4j-core"          % "4.0.2",
  "com.twitter"              % "bijection-util_2.11"     % "0.7.2"
)
 