
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.14"

lazy val root = (project in file("."))
  .settings(
    name := "samba-assignment",
    libraryDependencies ++= List(
      "org.typelevel" %% "cats-effect" % "3.5.7",
      "org.scalatest" %% "scalatest" % scalaTestVersion % Test,
      "org.typelevel" %% "cats-effect-testing-scalatest" % scalaTestCatsEffectVersion % Test
    )
  )

lazy val scalaTestVersion = "3.2.12"
lazy val scalaTestCatsEffectVersion = "1.4.0"
