name := "throttling-service-leaky-bucket"

version := "0.1"

scalaVersion := "2.13.2"

libraryDependencies ++= {
  val akkaVersion = "2.6.6"
  val akkaHttpVersion = "10.1.12"
  Seq(
    "com.typesafe.akka"  %% "akka-actor-typed"          % akkaVersion,
    "com.typesafe.akka"  %% "akka-stream"               % akkaVersion,
    "com.typesafe.akka"  %% "akka-http"                 % akkaHttpVersion,
    "com.typesafe.akka"  %% "akka-http-spray-json"      % akkaHttpVersion,
    "com.typesafe.akka"  %% "akka-slf4j"                % akkaVersion,
    "ch.qos.logback"     %  "logback-classic"           % "1.2.3",
    "com.typesafe.akka"  %% "akka-actor-testkit-typed"  % akkaVersion      % Test,
    "com.typesafe.akka"  %% "akka-http-testkit"         % akkaHttpVersion  % Test,
    "org.scalatest"      %% "scalatest"                 % "3.1.1"          % Test
  )
}

mainClass in assembly := Some("com.service.Main")
assemblyJarName in assembly := "application.jar"

