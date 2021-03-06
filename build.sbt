lazy val V = _root_.scalafix.sbt.BuildInfo

inThisBuild(
  List(
    organization := "com.github.takayahilton",
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "com.github.takayahilton",
        "takaya hilton",
        "takayahilton@gmail.com",
        url("https://github.com/takayahilton/scalafix-remove-unused-default-argument")
      )
    ),
    scalaVersion := V.scala212,
    crossScalaVersions := List(V.scala212, "2.11.12"),
    addCompilerPlugin(scalafixSemanticdb),
    scalacOptions ++= List(
      "-Yrangepos",
      "-Ywarn-unused"
    )
  )
)

skip in publish := true

lazy val rules = project.settings(
  moduleName := "scalafix",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  skip in publish := true
)

lazy val output = project.settings(
  skip in publish := true
)

lazy val tests = project
  .settings(
    skip in publish := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    compile.in(Compile) := 
      compile.in(Compile).dependsOn(compile.in(input, Compile)).value,
    scalafixTestkitOutputSourceDirectories :=
      sourceDirectories.in(output, Compile).value,
    scalafixTestkitInputSourceDirectories :=
      sourceDirectories.in(input, Compile).value,
    scalafixTestkitInputClasspath :=
      fullClasspath.in(input, Compile).value,
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)