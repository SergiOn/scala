package lectures.part3fp

object WhatsAFunction extends App {

  // DREAM: use functions as first class elements
  // problem: oop

  val doubler = new MyFunction[Int, Int] {
    override def apply(element: Int): Int = element * 2
  }

  println(doubler(2))

  // function types = Function1[A, B]
  val stringToIntConverter = new Function1[String, Int] {
    override def apply(string: String): Int = string.toInt
  }

  println(stringToIntConverter("3") + 4)

//  val adder: Function2[Int, Int, Int] = new Function2[Int, Int, Int] {
  val adder: ((Int, Int) => Int) = new Function2[Int, Int, Int] {
    override def apply(a: Int, b: Int): Int = a + b
  }

  println(stringToIntConverter("3") + 4)

  // Functions types Function2[A, B, R] === (A, B) => R

  // ALL SCALA FUNCTIONS ARE OBJECTS


  def concatenator: (String, String) => String = new Function2[String, String, String] {
    override def apply(a: String, b: String): String = a + b
  }

  println(concatenator("Hello ", "Scala"))

  // Function1[Int, Function1[Int, Int]]

  val supperAdder: Function1[Int, Function1[Int, Int]] = new Function1[Int, Function1[Int, Int]] {
    override def apply(x: Int): Function1[Int, Int] = new Function1[Int, Int] {
      override def apply(y: Int): Int = x + y
    }
  }

  val adder3 = supperAdder(3)

  println(adder3(4))
  println(supperAdder(3)(4))  // curried functions

}

//class Action {
//  def execute(element: Int): String = ???
//}

//trait Action[A, B] {
//  def execute(element: A): B
//}

trait MyFunction[A, B] {
  def apply(element: A): B
}
