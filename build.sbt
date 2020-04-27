name := "akka-grpc-quickstart-scala"

version := "1.0"

ThisBuild / turbo := true
val avroVersion = "1.8.2"

ThisBuild / parallelExecution := false
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val akkaVersion = "2.6.4"
lazy val akkaGrpcVersion = "0.8.4"


// common setting
lazy val commonSettings = Seq(
  scalaVersion := "2.13.1",
  organization := "com.datahub",
  homepage := Some(url("https://github.com/locona/datahub")),
  crossPaths := false,
  cancelable in Global := true,
  resolvers += DefaultMavenRepository,
  unmanagedBase in Test := baseDirectory.value.getParentFile / "cdata" / "oemkey",
  unmanagedBase in Runtime := baseDirectory.value.getParentFile / "cdata" / "oemkey",
  resolvers += "Artima Maven Repository" at "https://repo.artima.com/releases",
  addCompilerPlugin(scalafixSemanticdb), // enable SemanticDB
  scalacOptions ++= List(
    "-deprecation",
    "-Yrangepos",   // required by SemanticDB compiler plugin
    "-Ywarn-unused" // required by `RemoveUnused` rule
  )
)

lazy val `datahub-grpc`: Project = project
  .in(file("datahub-grpc"))
  .settings(commonSettings)
  .settings(
    crossScalaVersions += "2.13.1",
    description := "Scio - A Scala API for Apache Beam and Google Cloud Dataflow",
    // ALPN agent
    javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.10" % "runtime;test",
    resources in Compile ++= Seq(
      (baseDirectory in ThisBuild).value / "build.sbt",
      (baseDirectory in ThisBuild).value / "version.sbt"
    ),
    libraryDependencies ++= Seq(
      "org.apache.avro" % "avro" % avroVersion,
      "com.typesafe.akka" %% "akka-discovery" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
      "org.scalatest" %% "scalatest" % "3.0.8" % "test"
    )
  )
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(JavaAgent)
