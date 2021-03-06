name := "command-line-driven-text-search-engine"

version := "0.1"

scalaVersion := "2.13.3"

libraryDependencies ++= {
  Seq(
    "org.scalatest" %% "scalatest" % "3.2.2" % Test
  )
}

mainClass in assembly := Some("test.Main")
assemblyJarName in assembly := "app.jar"
