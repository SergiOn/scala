package exercises

abstract class MyList3[+A] {

  /*
    head = first element of the list
    tail = remainder of the list
    isEmpty = is this list Empty3
    add(int) => new list with this element added
    toString => a string representation of the list
  */

  def head: A
  def tail: MyList3[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyList3[B]
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"

  // higher-order functions
  def map[B](transformer: A => B): MyList3[B]
  def flatMap[B](transformer: A => MyList3[B]): MyList3[B]
  def filter(predicate: A => Boolean): MyList3[A]

  // concatenation
  def ++[B >: A](list: MyList3[B]): MyList3[B]
}

//object Empty3 extends MyList3[Nothing] {
case object Empty3 extends MyList3[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList3[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList3[B] = new Cons3(element, Empty3)
  def printElements: String = ""

  def map[B](transformer: Nothing => B): MyList3[B] = Empty3
  def flatMap[B](transformer: Nothing => MyList3[B]): MyList3[B] = Empty3
  def filter(predicate: Nothing => Boolean): MyList3[Nothing] = Empty3

  def ++[B >: Nothing](list: MyList3[B]): MyList3[B] = list
}

//class Cons3[+A](h: A, t: MyList3[A]) extends MyList3[A] {
case class Cons3[+A](h: A, t: MyList3[A]) extends MyList3[A] {
  def head: A = h
  def tail: MyList3[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList3[B] = new Cons3(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  /*
    [1, 2, 3].filter(n % 2 == 0) =
      [2, 3].filter(n % 2 == 0) =
      = new Cons3(2, [3].filter(n % 2 == 0))
      = new Cons3(2, Empty3.filter(n % 2 == 0))
      = new Cons3(2, Empty3)
  */

  def filter(predicate: A => Boolean): MyList3[A] =
//    if (predicate.apply(h)) new Cons3(h, t.filter(predicate))
    if (predicate(h)) new Cons3(h, t.filter(predicate))
    else t.filter(predicate)

  /*
    [1, 2, 3].map(n * 2)
      = new Cons3(2, new Cons3(4, [3].map(n * 2)))
      = new Cons3(2, new Cons3(4, new Cons3(6, Empty3.map(n * 2))))
      = new Cons3(2, new Cons3(4, new Cons3(6, Empty3)))
  */

  def map[B](transformer: A => B): MyList3[B] =
    new Cons3(transformer(h), t.map(transformer))

  /*
    [1, 2] ++ [3, 4, 5]
    = new Cons3(1, [2] ++ [3, 4, 5])
    = new Cons3(1, new Cons3(2, Empty3 ++ [3, 4, 5]))
    = new Cons3(1, new Cons3(2, [3, 4, 5]))
    = new Cons3(1, new Cons3(2, new Cons3(3, new Cons3(4, new Cons3(5)))))
  */

  def ++[B >: A](list: MyList3[B]): MyList3[B] = new Cons3(h, t ++ list)

  /*
    [1, 2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty3.flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty3
    = [1, 2, 2, 3]

  */

  def flatMap[B](transformer: A => MyList3[B]): MyList3[B] =
    transformer(h) ++ t.flatMap(transformer)
}

//trait MyPredicate[-T] {  // T => Boolean
//  def test(elem: T): Boolean
//}
//
//trait MyTransformer[-A, B] {  // A => B
//  def transform(elem: A): B
//}

object ListTest3 extends App {

  val listOfIntegers: MyList3[Int] = new Cons3(1, new Cons3(2, new Cons3(3, Empty3)))
  val cloneListOfIntegers: MyList3[Int] = new Cons3(1, new Cons3(2, new Cons3(3, Empty3)))
  val anotherListOfIntegers: MyList3[Int] = new Cons3(4, new Cons3(5, Empty3))
  val listOfStrings: MyList3[String] = new Cons3("Hello", new Cons3("Scala", Empty3))

  println(listOfIntegers.toString)
  println(listOfStrings.toString)


  println(listOfIntegers.map(new Function1[Int, Int] {
    override def apply(elem: Int): Int = elem * 2
  }).toString)

  println(listOfIntegers.filter(new Function1[Int, Boolean] {
    override def apply(elem: Int): Boolean = elem % 2 == 0
  }).toString)

  println((listOfIntegers ++ anotherListOfIntegers).toString)

  println(listOfIntegers.flatMap(new Function1[Int, MyList3[Int]] {
    override def apply(elem: Int): MyList3[Int] = new Cons3(elem, new Cons3(elem + 1, Empty3))
  }).toString)

  println(cloneListOfIntegers == listOfIntegers)

}
