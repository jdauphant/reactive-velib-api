name := """play-akka-demo"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  ws,
  specs2 % Test
)

routesGenerator := InjectedRoutesGenerator
