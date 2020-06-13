package lectures.part3fp

import scala.util.Random

object Options extends App {

  val myFirstOption: Option[Int] = Some(4)
  val noOption: Option[Int] = None

  println(myFirstOption)
  println(noOption)

  // WORK with unsafe API

  def unsafeMethod(): String = null

//  val result = Some(unsafeMethod())  // wrong
//  val result = Some(null)  // wrong

  val result: Option[String] = Option(unsafeMethod())  // Some or None

  println(result)

  // chained methods

  def backupMethod(): String = "A valid result"

  val chainedResult: Option[String] = Option(unsafeMethod()).orElse(Option(backupMethod()))

  // DESIGN unsafe APIs

  def betterUnsafeMethod(): Option[String] = None

  def betterBackupMethod(): Option[String] = Some("A valid result")

  val betterChainedResult: Option[String] = betterUnsafeMethod() orElse betterBackupMethod()

  // functions on options

  println(myFirstOption.isEmpty)

  println(myFirstOption.get)  // UNSAFE - DO NOT USE THIS

  // map, flatMap, filter

  println(myFirstOption.map(_ * 2))

  println(myFirstOption.filter(x => x > 10))

  println(myFirstOption.flatMap(x => Option(x * 10)))

  // for-comprehensions

  val config: Map[String, String] = Map(
    // fetched from elsewhere
    "host" -> "176.45.36.1",
    "port" -> "80",
  )

  class Connection {

    def connect: String = "Connected"  // connect to some server
  }

  object Connection {

    val random: Random = new Random(System.nanoTime())

    def apply(host: String, port: String): Option[Connection] =
      if (random.nextBoolean()) Some(new Connection)
      else None
  }

  // try to establish a connections, if so - print the connect method

  val host: Option[String] = config.get("host")
  val port: Option[String] = config.get("port")

  /*
    if (h != null)
      if (p != null)
        return Connection.apply(h, p)

    return null
  */

  val connection: Option[Connection] = host.flatMap(h => port.flatMap(p => Connection.apply(h, p)))

  /*
   if (c != null)
       return c.connect()
   return null
 */

  val connectionStatus: Option[String] = connection.map(c => c.connect)

  // if (connectionStatus == null) println(None) else print (Some(connectionStatus.get))

  println(connectionStatus)

  /*
   if (status != null)
    println(status)
 */

  connectionStatus.foreach(println)


  // chained calls

  config.get("host")
    .flatMap(host => config.get("port")
      .flatMap(port => Connection.apply(host, port))
      .map(connection => connection.connect))
    .foreach(println)

  // for-comprehensions

  val forConnectionStatus = for {
    host <- config.get("host")
    port <- config.get("port")
    connection <- Connection(host, port)
  } yield connection.connect

  forConnectionStatus.foreach(println)

}
