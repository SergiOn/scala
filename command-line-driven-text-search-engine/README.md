# Command Line Driven Text Search Engine

## Environment dependencies

* Java JDK with version 11 or 8
* Scala: 2.13.3
* Sbt: 1.3.13

## Setup environment

* Install JDK with correct version (11 or 8): https://jdk.java.net/archive/
* Install Scala: https://www.scala-lang.org/download
* Install sbt: https://www.scala-sbt.org/download.html

## How to start

* Run sbt
```bash
sbt
```

* Run the `runMain` command with path to the `Main` file `test.Main`
and with a path to the directory that containing text files.
For example:
```bash
runMain test.Main /foo/bar
```

* To exit the program run the command `control + c` on Mac or `ctrl + c` on Windows operation system

## Package to the java jar

* Run the command:
```bash
sbt assembly
```

* You can find the `jar` int the `target` folder [target/scala-2.13](./target/scala-2.13) with name `app.jar`.
You can define a jar name in the file [build.sbt](./build.sbt)

* To start the `jar` run a command:
```bash
java -jar app.jar
```
