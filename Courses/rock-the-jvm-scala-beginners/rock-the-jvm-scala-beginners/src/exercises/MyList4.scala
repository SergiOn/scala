package exercises

abstract class MyList4[+A] {

  /*
    head = first element of the list
    tail = remainder of the list
    isEmpty = is this list Empty4
    add(int) => new list with this element added
    toString => a string representation of the list
  */

  def head: A
  def tail: MyList4[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyList4[B]
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"

  // higher-order functions
  def map[B](transformer: A => B): MyList4[B]
  def flatMap[B](transformer: A => MyList4[B]): MyList4[B]
  def filter(predicate: A => Boolean): MyList4[A]

  // concatenation
  def ++[B >: A](list: MyList4[B]): MyList4[B]
}

//object Empty4 extends MyList4[Nothing] {
case object Empty4 extends MyList4[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList4[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList4[B] = new Cons4(element, Empty4)
  def printElements: String = ""

  def map[B](transformer: Nothing => B): MyList4[B] = Empty4
  def flatMap[B](transformer: Nothing => MyList4[B]): MyList4[B] = Empty4
  def filter(predicate: Nothing => Boolean): MyList4[Nothing] = Empty4

  def ++[B >: Nothing](list: MyList4[B]): MyList4[B] = list
}

//class Cons4[+A](h: A, t: MyList4[A]) extends MyList4[A] {
case class Cons4[+A](h: A, t: MyList4[A]) extends MyList4[A] {
  def head: A = h
  def tail: MyList4[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList4[B] = new Cons4(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  /*
    [1, 2, 3].filter(n % 2 == 0) =
      [2, 3].filter(n % 2 == 0) =
      = new Cons4(2, [3].filter(n % 2 == 0))
      = new Cons4(2, Empty4.filter(n % 2 == 0))
      = new Cons4(2, Empty4)
  */

  def filter(predicate: A => Boolean): MyList4[A] =
//    if (predicate.apply(h)) new Cons4(h, t.filter(predicate))
    if (predicate(h)) new Cons4(h, t.filter(predicate))
    else t.filter(predicate)

  /*
    [1, 2, 3].map(n * 2)
      = new Cons4(2, new Cons4(4, [3].map(n * 2)))
      = new Cons4(2, new Cons4(4, new Cons4(6, Empty4.map(n * 2))))
      = new Cons4(2, new Cons4(4, new Cons4(6, Empty4)))
  */

  def map[B](transformer: A => B): MyList4[B] =
    new Cons4(transformer(h), t.map(transformer))

  /*
    [1, 2] ++ [3, 4, 5]
    = new Cons4(1, [2] ++ [3, 4, 5])
    = new Cons4(1, new Cons4(2, Empty4 ++ [3, 4, 5]))
    = new Cons4(1, new Cons4(2, [3, 4, 5]))
    = new Cons4(1, new Cons4(2, new Cons4(3, new Cons4(4, new Cons4(5)))))
  */

  def ++[B >: A](list: MyList4[B]): MyList4[B] = new Cons4(h, t ++ list)

  /*
    [1, 2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty4.flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty4
    = [1, 2, 2, 3]

  */

  def flatMap[B](transformer: A => MyList4[B]): MyList4[B] =
    transformer(h) ++ t.flatMap(transformer)
}

//trait MyPredicate[-T] {  // T => Boolean
//  def test(elem: T): Boolean
//}
//
//trait MyTransformer[-A, B] {  // A => B
//  def transform(elem: A): B
//}

object ListTest4 extends App {

  val listOfIntegers: MyList4[Int] = new Cons4(1, new Cons4(2, new Cons4(3, Empty4)))
  val cloneListOfIntegers: MyList4[Int] = new Cons4(1, new Cons4(2, new Cons4(3, Empty4)))
  val anotherListOfIntegers: MyList4[Int] = new Cons4(4, new Cons4(5, Empty4))
  val listOfStrings: MyList4[String] = new Cons4("Hello", new Cons4("Scala", Empty4))

  println(listOfIntegers.toString)
  println(listOfStrings.toString)


//  println(listOfIntegers.map(elem => elem * 2).toString)
  println(listOfIntegers.map(_ * 2).toString)

//  println(listOfIntegers.filter((elem: Int) => elem % 2 == 0).toString)
  println(listOfIntegers.filter(_ % 2 == 0).toString)

  println((listOfIntegers ++ anotherListOfIntegers).toString)

  println(listOfIntegers.flatMap(elem => new Cons4(elem, new Cons4(elem + 1, Empty4))).toString)

  println(cloneListOfIntegers == listOfIntegers)

}
