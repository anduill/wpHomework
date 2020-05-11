name := "wp_engine_account_resolve"
version := "0.1.0-SNAPSHOT"

scalaVersion := "2.12.11"

scalacOptions ++= Seq("-deprecation", "-feature", "-Ypartial-unification")

mainClass in Compile := Some("org.wpengine.account.resolver.WPMerge")

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-blaze-server" % "0.18.9",
  "org.http4s" %% "http4s-blaze-client" % "0.18.9",
  "org.http4s" %% "http4s-circe" % "0.18.9",
  "org.http4s" %% "http4s-dsl" % "0.18.9",
  "org.http4s" %% "http4s-client" % "0.18.9",
  "org.typelevel" %% "cats-core" % "2.0.0-M4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2",
  "com.github.pureconfig" %% "pureconfig" % "0.12.3",
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "io.circe" %% "circe-optics" % "0.9.3",
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "org.scalatest" %% "scalatest" % "3.0.4" % Test)

resolvers += "Sonatype snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
resolvers += Resolver.bintrayRepo("zamblauskas", "maven")
