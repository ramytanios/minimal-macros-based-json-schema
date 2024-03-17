ThisBuild / scalaVersion := "2.13.12"

lazy val circeVersion = "0.14.6"
lazy val circeDeps = Seq(
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion
)

lazy val XfatalWarnings = (scalacOptions -= "-Xfatal-warnings")

lazy val root =
  (project in file(".")).aggregate(annotations.jvm, schema, demo)

lazy val annotations = crossProject(JVMPlatform, JSPlatform)
  .in(file("annotations"))
  .settings(
    name := "schema-lib-annotations",
    XfatalWarnings
  )

lazy val schema = project
  .in(file("schema"))
  .settings(
    name := "schema-lib",
    libraryDependencies ++= circeDeps,
    XfatalWarnings
  )
  .dependsOn(annotations.jvm)

lazy val demo = project
  .in(file("demo"))
  .settings(
    XfatalWarnings
  )
  .dependsOn(schema)
