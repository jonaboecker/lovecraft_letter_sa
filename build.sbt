import scala.language.postfixOps

val scala3Version = "3.3.3"

val akkaVersion = "2.8.5"
val akkaHttpVersion = "10.5.3"

val controllerVersion = "0.1.0-SNAPSHOT"
val effectHandlerVersion = "0.1.0-SNAPSHOT"
val initializerVersion = "0.1.0-SNAPSHOT"

lazy val dependencies  = Seq(
  version := "0.1.0-SNAPSHOT",
  scalaVersion := scala3Version,
  libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.18",
  libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test",
  libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
  libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.4",
  libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.3.0" % "provided",
  dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0",
  dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.17.0",
  dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.17.0",
  libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.17.0",
  libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.17.0",
  libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.17.0",
  libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  libraryDependencies += "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.2"
)

lazy val controller = (project in file("controller"))
  .settings(
    name := "controller",
    version := controllerVersion,
    dependencies
  )

lazy val effectHandler = (project in file("effectHandler"))
  .settings(
    name := "effectHandler",
    version := effectHandlerVersion,
    dependencies
  )

lazy val initializer = (project in file("initializer"))
  .settings(
    name := "initializer",
    version := initializerVersion,
    dependencies
  )

lazy val root = (project in file("."))
  .aggregate(controller, effectHandler, initializer)
  .settings(
    name := "lovecraftletter",
    publish / skip := true,
    dependencies
  )
  .enablePlugins(JacocoCoverallsPlugin)

ThisBuild / assemblyMergeStrategy := {
  case "module-info.class" => MergeStrategy.last
  case "META-INF/versions/9/module-info.class" => MergeStrategy.last
  case x =>
    val oldStrategy = assemblyMergeStrategy.value
    oldStrategy(x)
}