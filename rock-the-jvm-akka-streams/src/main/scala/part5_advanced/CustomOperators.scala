package part5_advanced

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream._
import akka.stream.stage._

import scala.collection.mutable
import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Random, Success}

object CustomOperators extends App {

  implicit val system: ActorSystem = ActorSystem("CustomOperators")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // 1 - a custom source which emits random numbers until canceled

  class RandomNumberGenerator(max: Int) extends GraphStage[/*step 0: define the shape*/SourceShape[Int]] {

    // step 1: define the ports and the component-specific members
    val outPort: Outlet[Int] = Outlet[Int]("randomGenerator")
    val random = new Random()

    // step 2: construct a new shape
    override def shape: SourceShape[Int] = SourceShape(outPort)

    // step 3: create the logic
    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {
      // step 4:
      // define mutable state
      // implement my logic here

      setHandler(outPort, new OutHandler {
        // when there is demand from downstream
        override def onPull(): Unit = {
          // emit a new element
          val nextNumber = random.nextInt(max)
          // push it out of the outPort
          push(outPort, nextNumber)
        }
      })
    }
  }

  val randomGeneratorSource = Source.fromGraph(new RandomNumberGenerator(100))
  //  randomGeneratorSource.runWith(Sink.foreach(println))

  // 2 - a custom sink that prints elements in batches of a given size

  class Batcher(batchSize: Int) extends GraphStage[SinkShape[Int]] {

    val inPort: Inlet[Int] = Inlet[Int]("batcher")

    override def shape: SinkShape[Int] = SinkShape[Int](inPort)

    override def createLogic(inheritedAttributes: Attributes): GraphStageLogic = new GraphStageLogic(shape) {

      override def preStart(): Unit = {
        pull(inPort)
      }

      // mutable state
      val batch = new mutable.Queue[Int]

      setHandler(inPort, new InHandler {
        // when the upstream wants to send me an element
        override def onPush(): Unit = {
          val nextElement = grab(inPort)
          batch.enqueue(nextElement)

          // assume some complex computation
          Thread.sleep(100)

          if(batch.size >= batchSize) {
            println("New batch: " + batch.dequeueAll(_ => true).mkString("[", ", ", "]"))
          }

          pull(inPort) // send demand upstream
        }

        override def onUpstreamFinish(): Unit = {
          if (batch.nonEmpty) {
            println("New batch: " + batch.dequeueAll(_ => true).mkString("[", ", ", "]"))
            println("Stream finished.")
          }
        }
      })
    }
  }

  val batcherSink = Sink.fromGraph(new Batcher(10))
   randomGeneratorSource.to(batcherSink).run()

}
