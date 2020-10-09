name := "akka-grpc-quickstart-scala"

version := "1.0"

ThisBuild / turbo := true
val avroVersion = "1.10.0"

ThisBuild / parallelExecution := false
Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val akkaVersion     = "2.6.10"
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
    "-Yrangepos",    // required by SemanticDB compiler plugin
    "-Ywarn-unused", // required by `RemoveUnused` rule
    "-Ywarn-unused:imports"
  ),
  wartremoverErrors in (Compile, compile) ++= Seq(
    Wart.ArrayEquals,
    Wart.AnyVal,
    Wart.Enumeration,
    Wart.ExplicitImplicitTypes,
    Wart.FinalCaseClass,
    Wart.FinalVal,
    Wart.LeakingSealed,
    Wart.Serializable,
    Wart.Return
  ),
  wartremoverExcluded += sourceManaged.value
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
      "org.apache.avro"   % "avro"                 % avroVersion,
      "com.typesafe.akka" %% "akka-discovery"      % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % "test",
      "org.scalatest"     %% "scalatest"           % "3.2.2" % "test"
    )
  )
  .enablePlugins(AkkaGrpcPlugin)
  .enablePlugins(JavaAgent)

addCommandAlias(
  "check",
  ";scalafmtCheckAll;scalafmtSbtCheck;compile:scalafix --check;test:scalafix --check"
)
addCommandAlias("fmt", ";scalafmtAll;scalafmtSbt")
addCommandAlias("scalafixAll", ";compile:scalafix;test:scalafix")
