val scala3Version = "3.2.2-RC1-bin-20221101-d84007c-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "lovecraftletter",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.14",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0"
  )
  .enablePlugins(JacocoCoverallsPlugin)
