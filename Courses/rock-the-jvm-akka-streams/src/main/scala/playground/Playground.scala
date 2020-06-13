package playground

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}

object Playground extends App {

  implicit val system = ActorSystem("AkkaStreamsDemo")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  Source.single("hello, Streams!").to(Sink.foreach(println)).run()

}
