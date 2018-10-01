package lectures.part2oop

object Exceptions extends App {

  val x: String = null

//  println(x.length)

  // this ^^ will crash with NPE

  // 1. throwing exceptions


//  throw new NullPointerException

//  val aWeirdValue = throw new NullPointerException  // type Never

  // throwable classes extend the Throwable class
  // Exception and Error are the major Throwable subtypes

  // 2. how to catch exceptions

  def getInt(withExeptions: Boolean): Int =
    if (withExeptions) throw new RuntimeException("No int for you!")
    else 42


//  try {
//    getInt(true)
//  } catch {
////    case e: RuntimeException => println("caught a Runtime exceptions")
//    case e: NullPointerException => println("caught a Runtime exceptions")
//  } finally {
//    println("finally")
//  }

  val potentialFail = try {

    // code that might throw

    getInt(true)

  } catch {

    case e: RuntimeException => 43
//    case e: RuntimeException => println("caught a Runtime exceptions")
//    case e: NullPointerException => println("caught a Runtime exceptions")

  } finally {

    // code that will get executed NO MATTER WHAT
    // optional
    // does not influence the return type of this expression
    // use finally only for side effects

    println("finally")

  }

  println(potentialFail)


  // 3. how to define your own exceptions

  class MyException extends Exception

  val exception = new MyException

//  throw exception


  // OOM
//  val array = Array.ofDim(Int.MaxValue)

  // SO
//  def infinite: Int = 1 + infinite
//  val noLimit = infinite


  class OverflowException extends RuntimeException
  class UnderflowException extends RuntimeException
  class MathCalculationException extends RuntimeException("Division by 0")

  object PocketCalculator {

    def add(x: Int, y: Int) = {
      val result = x + y

      if (x > 0 && y > 0 && result < 0) throw new OverflowException
      else if (x < 0 && y < 0 && result > 0) throw new UnderflowException
      else result
//      result
    }

    def subtract(x: Int, y: Int) = {
      val result = x - y

      if (x > 0 && y < 0 && result < 0) throw new OverflowException
      else if (x < 0 && y > 0 && result > 0) throw new UnderflowException
      else result
    }

    def multiply(x: Int, y: Int) = {
      val result = x * y

      if (x > 0 && y > 0 && result < 0) throw new OverflowException
      else if (x < 0 && y < 0 && result < 0) throw new OverflowException
      else if (x > 0 && y < 0 && result > 0) throw new UnderflowException
      else if (x < 0 && y > 0 && result > 0) throw new UnderflowException
      else result
    }

    def divide(x: Int, y: Int) = {
      if (y == 0) throw new MathCalculationException
      else x / 7
    }

  }

//  println(PocketCalculator.add(Int.MaxValue, 10))

  println(PocketCalculator.divide(2, 0))

}
