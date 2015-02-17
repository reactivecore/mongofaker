import SonatypeKeys._

sonatypeSettings

name := "mongofaker"

organization := "net.reactivecore"

profileName := "net.reactivecore"

version := "0.2.1"

// To sync with Maven central, you need to supply the following information:
pomExtra := {
  <url>https://github.com/reactivecore/mongofaker</url>
    <licenses>
      <license>
        <name>MIT License</name>
        <url>http://opensource.org/licenses/MIT</url>
      </license>
    </licenses>
    <scm>
      <connection>scm:git:@github.com:reactivecore/mongofaker.git</connection>
      <url>git@github.com:reactivecore/mongofaker.git</url>
    </scm>
    <developers>
      <developer>
        <id>nob13</id>
        <name>Norbert Schultz</name>
        <url>https://www.reactivecore.de</url>
      </developer>
    </developers>
}

scalaVersion := "2.10.3"

crossScalaVersions := Seq("2.10.3", "2.11.5")

scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.specs2" %% "specs2" % "2.3.11" % "test"
// Temporary while development, because stupid sbt doesn't add the Lib to IDEA
// libraryDependencies += "org.specs2" %% "specs2" % "2.3.11"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.7"

libraryDependencies += "org.slf4j" % "slf4j-simple" % "1.7.7" % "test"
