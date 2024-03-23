ThisBuild / scalaVersion := "2.13.12"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

lazy val V = new {
  val circe = "0.14.6"
  val cats = "2.10.0"
}

lazy val circeVersion = "0.14.6"

lazy val root =
  (project in file(".")).aggregate(annotations.jvm, schema, examples)

lazy val annotations = crossProject(JVMPlatform, JSPlatform)
  .in(file("annotations"))
  .settings(
    name := "schema-lib-annotations",
    libraryDependencies ++= Seq("io.circe" %% "circe-core" % V.circe),
    scalacOptions -= "-Xfatal-warnings"
  )

lazy val schema = project
  .in(file("schema"))
  .settings(
    name := "schema-lib",
    libraryDependencies ++=
      Seq(
        "io.circe" %% "circe-generic" % V.circe,
        "io.circe" %% "circe-literal" % V.circe,
        "io.circe" %% "circe-parser" % V.circe,
        "org.typelevel" %% "cats-core" % V.cats,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
      ),
    scalacOptions -= "-Xfatal-warnings",
    scalacOptions += "-Ywarn-macros:after",
    scalacOptions += "-Ymacro-annotations"
  )
  .dependsOn(annotations.jvm)

lazy val examples = project
  .in(file("examples"))
  .settings(
    scalacOptions -= "-Xfatal-warnings"
  )
  .dependsOn(schema)
