val scala3Version = "3.3.1"
//val scala3Version = "3.2.2-RC2"
//val scala3Version = "3.2.2-RC1-bin-20221101-d84007c-NIGHTLY"

lazy val root = project
  .in(file("."))
  .settings(
    name := "lovecraftletter",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.16",
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test",
    libraryDependencies += "org.scala-lang.modules" %% "scala-swing" % "3.0.0",
    libraryDependencies += "com.typesafe.play" %% "play-json" % "2.10.1",
    libraryDependencies += "org.scala-lang.modules" %% "scala-xml" % "2.1.0" % "provided",
      dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3",
      dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.14.3",
      dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-core" % "2.14.3",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.3",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-annotations" % "2.14.3",
      libraryDependencies += "com.fasterxml.jackson.core" % "jackson-core" % "2.14.3"

  )
  .enablePlugins(JacocoCoverallsPlugin)

ThisBuild / assemblyMergeStrategy := {
    case "module-info.class" => MergeStrategy.last
    case "META-INF/versions/9/module-info.class" => MergeStrategy.last
    case x =>
        val oldStrategy = (assemblyMergeStrategy).value
        oldStrategy(x)
}