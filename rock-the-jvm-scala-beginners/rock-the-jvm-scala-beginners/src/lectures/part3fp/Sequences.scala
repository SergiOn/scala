package lectures.part3fp

import scala.util.Random

object Sequences extends App {

  // Seq
  val aSequences = Seq(1, 3, 2, 4)
  println(aSequences)
  println(aSequences.reverse)
  println(aSequences(2))
  println(aSequences ++ Seq(7, 5, 6))
  println(aSequences)
  println(aSequences.sorted)

  // Ranges
  val aRanges: Seq[Int] = 1 to 10
  println(aRanges)
  aRanges.foreach(println)

  (1 to 10).foreach(x => println("Hello"))

  // lists
  val aList = List(1, 2, 3)
  val prepended = 42 :: aList
  val prepended2 = 42 +: aList
  val prepended3 = 42 +: aList :+ 89
  println(prepended)
  println(prepended2)
  println(prepended3)

  val apples5 = List.fill(5)("apple")
  println(apples5)

  println(aList.mkString("-|-"))

  // arrays
  val numbers = Array(1, 2, 3, 4)
  val threeElements = Array.ofDim[Int](3)
  threeElements.foreach(println)

  // mutation
  numbers(2) = 0  // syntax sugar for numbers.update(2, 0)
  println(numbers.mkString(" "))

  // arrays and seq
  val numbersSeq: Seq[Int] = numbers  // implicit conversion
  println(numbersSeq)

  // vectors
  val vector: Vector[Int] = Vector(1, 2, 3)
  println(vector)

  // vectors vs lists

  val maxRuns = 1000
  val maxCapacity = 1000000

  def getWriteTime(collection: Seq[Int]): Double = {
    val r = new Random
    val times = for {
      it <- 1 to maxRuns
    } yield {
      val currentTime = System.nanoTime()
      collection.updated(r.nextInt(maxCapacity), r.nextInt())
      System.nanoTime() - currentTime
    }

    times.sum * 1.0 / maxRuns
  }


  val numbersList = (1 to maxCapacity).toList
  val numbersVector = (1 to maxCapacity).toVector

  // keeps reference to tail
  // updating an element in the middle takes long
  println(getWriteTime(numbersList))  // 8202337.234

  // depth of the tree is small
  // needs to replace an entire 32-element chunk
  println(getWriteTime(numbersVector))  // 7152.959

}
