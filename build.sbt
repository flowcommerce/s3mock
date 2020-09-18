import sbt.Keys.resolvers

name := "s3mock"

version := "0.2.25"

organization := "io.flow"

scalaVersion := "2.13.1"

licenses := Seq("MIT" -> url("https://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/flowcommerce/s3mock"))

// Must match typesafe play version
//   com.typesafe.akka:akka-actor-typed_2.13:2.6.3
// pulled in by
//   com.typesafe.play:play_2.13:2.8.1
val akkaVersion = "2.6.9"
val akkaHttpVersion = "10.1.10"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
  "org.scala-lang.modules" %% "scala-xml" % "1.3.0",
  "com.github.pathikrit" %% "better-files" % "3.9.1",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.11.864",
  "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0" % Test,
  "org.scalatest" %% "scalatest-shouldmatchers" % "3.2.2" % "test",
  "org.scalatest" %% "scalatest-flatspec" % "3.2.2" % "test",
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
