name := """reactive-velib-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

resolvers ++= Seq(
  "Scalaz Bintray Repo" at "https://dl.bintray.com/scalaz/releases"
)


libraryDependencies ++= Seq(
  ws,
  specs2 % Test
)

routesGenerator := InjectedRoutesGenerator
