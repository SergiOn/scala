package lectures.part3fp

object AnonymousFunctions extends App {

  val doublerOOp = new Function[Int, Int] {
    override def apply(x: Int): Int = x * 2
  }

  // anonymous function (LAMBDA)

//  val doubler = (x: Int) => x * 2

//  val doubler: Int => Int = (x: Int) => x * 2

  val doubler: Int => Int = x => x * 2

  // multiple params in a lambda

//  val adder = (a: Int, b: Int) => a + b

  val adder: (Int, Int) => Int = (a: Int, b: Int) => a + b

  // no params

//  val justDoSomething = () => 3

  val justDoSomething: () => Int = () => 3

  // careful

  println(justDoSomething)  // function itself
  println(justDoSomething())  // call

  // curly braces with lambdas

  val stringToInt = { (str: String) =>
    str.toInt
  }

  // MOAR syntactic sugar

//  val niceIncrementer: Int => Int = (x: Int) => x + 1

  val niceIncrementer: Int => Int = _ + 1  // equivalent to x => x + 1

//  val niceAdder: (Int, Int) => Int = (a, b) => a + b

  val niceAdder: (Int, Int) => Int = _ + _  // equivalent to (a, b) => a + b


  val superAdd = (x: Int) => (y: Int) => x + y

  println(superAdd(3)(4))

}
