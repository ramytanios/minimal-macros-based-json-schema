ThisBuild / scalaVersion := "2.13.12"

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.6.0"

lazy val circeVersion = "0.14.6"

lazy val root =
  (project in file(".")).aggregate(annotations.jvm, schema, demo)

lazy val annotations = crossProject(JVMPlatform, JSPlatform)
  .in(file("annotations"))
  .settings(
    name := "schema-lib-annotations",
    scalacOptions -= "-Xfatal-warnings"
  )

lazy val schema = project
  .in(file("schema"))
  .settings(
    name := "schema-lib",
    libraryDependencies ++=
      Seq(
        "io.circe" %% "circe-generic" % circeVersion,
        "io.circe" %% "circe-literal" % circeVersion,
        "io.circe" %% "circe-parser" % circeVersion,
        "org.scala-lang" % "scala-reflect" % scalaVersion.value
      ),
    scalacOptions -= "-Xfatal-warnings"
  )
  .dependsOn(annotations.jvm)

lazy val demo = project
  .in(file("demo"))
  .settings(
    scalacOptions -= "-Xfatal-warnings"
  )
  .dependsOn(schema)
