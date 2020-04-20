import sbt.Keys.resolvers

name := "s3mock"

version := "0.2.20"

organization := "io.flow"

scalaVersion := "2.13.1"

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/flowcommerce/s3mock"))

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.6.4",
  "com.typesafe.akka" %% "akka-http" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.6.4" % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
  "com.github.pathikrit" %% "better-files" % "3.8.0",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.761",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "test",
  "org.iq80.leveldb" % "leveldb" % "0.12",
  "com.lightbend.akka" %% "akka-stream-alpakka-s3" % "1.1.2" % "test"
)

libraryDependencies ++= {
  CrossVersion.partialVersion(scalaVersion.value) match {
    case Some((2, major)) if major >= 13 =>
      Seq("org.scala-lang.modules" %% "scala-parallel-collections" % "0.2.0" % "test")
    case _ =>
      Seq()
  }
}

parallelExecution in Test := false

publishMavenStyle := true

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
resolvers += "Artifactory" at "https://flow.jfrog.io/flow/libs-release/"
credentials += Credentials(
  "Artifactory Realm",
  "flow.jfrog.io",
  System.getenv("ARTIFACTORY_USERNAME"),
  System.getenv("ARTIFACTORY_PASSWORD"),
)

publishTo := {
  val host = "https://flow.jfrog.io/flow"
  if (isSnapshot.value) {
    Some("Artifactory Realm" at s"$host/libs-snapshot-local;build.timestamp=" + new java.util.Date().getTime)
  } else {
    Some("Artifactory Realm" at s"$host/libs-release-local")
  }
}
