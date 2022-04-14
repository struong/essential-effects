name := "essential-effects"

version := "0.1"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / fork := true
val CatsEffectVersion = "2.2.0"

testFrameworks += new TestFramework("munit.Framework")


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % CatsEffectVersion,
  "org.typelevel" %% "cats-effect-laws" % CatsEffectVersion % Test,
  "org.scalameta" %% "munit" % "0.7.29" % Test
)

// remove fatal warnings since exercises have unused and dead code blocks
scalacOptions --= Seq(
  "-Xfatal-warnings"
)
