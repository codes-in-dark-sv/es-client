//********************************************************
// Play settings
//*************

name := """finagle-elasticsearch"""

version := "1.0-SANTO"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws,
  "com.twitter"              % "finagle-http_2.11"       % "6.25.0",
  "com.twitter"              % "bijection-util_2.11"     % "0.7.2"
)

mainClass in assembly := Some("play.core.server.NettyServer")

fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

// Exclude commons-logging because it conflicts with the jcl-over-slf4j
libraryDependencies ~= { _ map {
  case m if m.organization == "com.typesafe.play" =>
    m.exclude("commons-logging", "commons-logging")
  case m => m
}}

// Take the first ServerWithStop because it's packaged into two jars
assemblyMergeStrategy in assembly := {
  case "play/core/server/ServerWithStop.class" => MergeStrategy.first
  case other => (assemblyMergeStrategy in assembly).value(other)
}