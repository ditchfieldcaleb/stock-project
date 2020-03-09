import play.core.PlayVersion.akkaVersion

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(
    name := "play-scala-websocket-example",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      guice,
      ws,
      "org.webjars" % "flot" % "0.8.3-1",
      "org.webjars" % "bootstrap" % "3.3.7",
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test,
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
      "org.awaitility" % "awaitility" % "4.0.1" % Test,
			"com.lihaoyi" %% "ujson" % "0.9.5",
			"org.scalaj" %% "scalaj-http" % "2.4.2"
    ),
    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-Xfatal-warnings"
    )
  )
