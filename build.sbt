name := "mongofaker"

organization := "net.reactivecore"

version := "0.2"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.11" % "test"
// Temporary while development, because stupid sbt doesn't add the Lib to IDEA
// libraryDependencies += "org.specs2" %% "specs2" % "2.3.11"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7" % "test"
