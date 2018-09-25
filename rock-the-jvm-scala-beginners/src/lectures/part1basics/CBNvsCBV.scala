package lectures.part1basics

object CBNvsCBV extends App {

  def calledByValue(x: Long): Unit = {
    println("by value: " + x)
    println("by value: " + x)
  }

  def calledByName(x: => Long): Unit = {
    println("by name:  " + x)
    println("by name:  " + x)
  }

  calledByValue(System.nanoTime())
  calledByName(System.nanoTime())


  def calledByValue2(x: Long): Unit = {
    println("by value: " + 36249074474100L)
    println("by value: " + 36249074474100L)
  }

  // Lazy evaluations, useful in lazy streams

  def calledByName2(x: => Long): Unit = {
    println("by name:  " + System.nanoTime())
    println("by name:  " + System.nanoTime())
  }

  calledByValue2(36249074474100L) // System.nanoTime()
  calledByName2(System.nanoTime())


  def infinite(): Int = 1 + infinite()
  def printFirst(x: Int, y: => Int): Unit = println(x)

//  printFirst(infinite(), 34)  // StackOverflowError
  printFirst(34, infinite())


}
