name := "rock-the-jvm-akka-essentials"

version := "0.1"

scalaVersion := "2.12.8"

//val akkaVersion = "2.5.13"
val akkaVersion = "2.5.20"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

