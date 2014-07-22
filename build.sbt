import sbt.Keys._

name := """PlayStartApp"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.1"

libraryDependencies ++= Seq(
  jdbc,
  javaEbean,
  cache,
  "org.sorm-framework" % "sorm" % "0.3.15",
  "org.mindrot" % "jbcrypt" % "0.3m",
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  filters
)

resolvers ++= Seq(
    "Apache" at "http://repo1.maven.org/maven2/",
    "jBCrypt Repository" at "http://repo1.maven.org/maven2/org/",
    "Sonatype OSS Snasphots" at "http://oss.sonatype.org/content/repositories/snapshots"
)


lazy val root = (project in file(".")).enablePlugins(play.PlayJava)
