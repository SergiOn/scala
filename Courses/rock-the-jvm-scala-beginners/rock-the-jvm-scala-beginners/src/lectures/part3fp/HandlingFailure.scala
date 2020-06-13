package lectures.part3fp

import scala.util.{Failure, Random, Success, Try}

object HandlingFailure extends App {

  // create success and failure
  val aSuccess: Success[Int] = Success(3)
  var aFailure: Failure[Nothing] = Failure(new RuntimeException("SUPER FAILURE"))

  println(aSuccess)
  println(aFailure)

  def unsafeMethod(): String = throw new RuntimeException("NO STRING FOR YOU")

  // Try objects via the apply method

  val potentialFailure: Try[String] = Try(unsafeMethod())

  println(potentialFailure)

  // syntax sugar

  val anotherPotentialFailure: Try[Unit] = Try {
    // code that might throw
  }

  // utilities

  println(potentialFailure.isSuccess)

  // orElse

  def backupMethod(): String = "A valid result"

  val fallbackTry: Try[String] = Try(unsafeMethod()).orElse(Try(backupMethod()))

  println(fallbackTry)

  // IF you design the API

  def betterUnsafeMethod(): Try[String] = Failure(new RuntimeException("NO STRING FOR YOU"))

  def betterBackupMethod(): Try[String] = Success("A valid result")

  val betterFallback: Try[String] = betterUnsafeMethod() orElse betterBackupMethod()

  // map, flatMap, filter

  println(aSuccess.map(_ * 2))

  println(aSuccess.flatMap(x => Success(x * 10)))

  println(aSuccess.filter(_ > 10))

  // for-comprehensions

  val host: String = "localhost"
  val port: String = "8080"

  def renderHTML(page: String): Unit = println(page)

  class Connection {

    def get(url: String): String = {
      val random: Random = new Random(System.nanoTime())

      if (random.nextBoolean()) "<html>...</html>"
      else throw new RuntimeException("Connection interrupted")
    }

    def getSafe(url: String): Try[String] = Try(get(url))
  }

  object HttpService {
    val random: Random = new Random(System.nanoTime())

    def getConnection(host: String, port: String): Connection =
      if (random.nextBoolean()) new Connection
      else throw new RuntimeException("Someone else took the port")

    def getSafeConnection(host: String, port: String): Try[Connection] = Try(getConnection(host, port))
  }

  val possibleConnection = HttpService.getSafeConnection(host, port)

  val possibleHTML = possibleConnection.flatMap(connection => connection.getSafe("/home"))

  possibleHTML.foreach(renderHTML)

  // shorthand version

  HttpService.getSafeConnection(host, port)
    .flatMap(connection => connection.getSafe("/home"))
    .foreach(renderHTML)

  // for-comprehensions version

  for {
    connection <- HttpService.getSafeConnection(host, port)
    html <- connection.getSafe("/home")
  } renderHTML(html)

  /*
    try {
      connection = HttpService.getSafeConnection(host, port)

      try {
        page = connection.getSafe("/home")
        renderHTML(page)
      } catch (some other exception) {

      }
    } catch (exception) {

    }
  */

}
