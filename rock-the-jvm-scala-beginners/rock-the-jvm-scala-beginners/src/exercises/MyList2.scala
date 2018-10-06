package exercises

abstract class MyList2[+A] {

  /*
    head = first element of the list
    tail = remainder of the list
    isEmpty = is this list Empty2
    add(int) => new list with this element added
    toString => a string representation of the list
  */

  def head: A
  def tail: MyList2[A]
  def isEmpty: Boolean
  def add[B >: A](element: B): MyList2[B]
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"

  def map[B](transformer: MyTransformer2[A, B]): MyList2[B]
  def flatMap[B](transformer: MyTransformer2[A, MyList2[B]]): MyList2[B]
  def filter(predicate: MyPredicate2[A]): MyList2[A]

  // concatenation
  def ++[B >: A](list: MyList2[B]): MyList2[B]
}

//object Empty2 extends MyList2[Nothing] {
case object Empty2 extends MyList2[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList2[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList2[B] = new Cons2(element, Empty2)
  def printElements: String = ""

  def map[B](transformer: MyTransformer2[Nothing, B]): MyList2[B] = Empty2
  def flatMap[B](transformer: MyTransformer2[Nothing, MyList2[B]]): MyList2[B] = Empty2
  def filter(predicate: MyPredicate2[Nothing]): MyList2[Nothing] = Empty2

  def ++[B >: Nothing](list: MyList2[B]): MyList2[B] = list
}

//class Cons2[+A](h: A, t: MyList2[A]) extends MyList2[A] {
case class Cons2[+A](h: A, t: MyList2[A]) extends MyList2[A] {
  def head: A = h
  def tail: MyList2[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList2[B] = new Cons2(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements

  /*
    [1, 2, 3].filter(n % 2 == 0) =
      [2, 3].filter(n % 2 == 0) =
      = new Cons2(2, [3].filter(n % 2 == 0))
      = new Cons2(2, Empty2.filter(n % 2 == 0))
      = new Cons2(2, Empty2)
  */

  def filter(predicate: MyPredicate2[A]): MyList2[A] =
    if (predicate.test(h)) new Cons2(h, t.filter(predicate))
    else t.filter(predicate)

  /*
    [1, 2, 3].map(n * 2)
      = new Cons2(2, new Cons2(4, [3].map(n * 2)))
      = new Cons2(2, new Cons2(4, new Cons2(6, Empty2.map(n * 2))))
      = new Cons2(2, new Cons2(4, new Cons2(6, Empty2)))
  */

  def map[B](transformer: MyTransformer2[A, B]): MyList2[B] =
    new Cons2(transformer.transform(h), t.map(transformer))

  /*
    [1, 2] ++ [3, 4, 5]
    = new Cons2(1, [2] ++ [3, 4, 5])
    = new Cons2(1, new Cons2(2, Empty2 ++ [3, 4, 5]))
    = new Cons2(1, new Cons2(2, [3, 4, 5]))
    = new Cons2(1, new Cons2(2, new Cons2(3, new Cons2(4, new Cons2(5)))))
  */

  def ++[B >: A](list: MyList2[B]): MyList2[B] = new Cons2(h, t ++ list)

  /*
    [1, 2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2].flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty2.flatMap(n => [n, n + 1])
    = [1, 2] ++ [2, 3] ++ Empty2
    = [1, 2, 2, 3]

  */

  def flatMap[B](transformer: MyTransformer2[A, MyList2[B]]): MyList2[B] =
    transformer.transform(h) ++ t.flatMap(transformer)
}

trait MyPredicate2[-T] {
  def test(elem: T): Boolean
}

trait MyTransformer2[-A, B] {
  def transform(elem: A): B
}

object ListTest2 extends App {

  val listOfIntegers: MyList2[Int] = new Cons2(1, new Cons2(2, new Cons2(3, Empty2)))
  val cloneListOfIntegers: MyList2[Int] = new Cons2(1, new Cons2(2, new Cons2(3, Empty2)))
  val anotherListOfIntegers: MyList2[Int] = new Cons2(4, new Cons2(5, Empty2))
  val listOfStrings: MyList2[String] = new Cons2("Hello", new Cons2("Scala", Empty2))

  println(listOfIntegers.toString)
  println(listOfStrings.toString)


  println(listOfIntegers.map(new MyTransformer2[Int, Int] {
    override def transform(elem: Int): Int = elem * 2
  }).toString)

  println(listOfIntegers.filter(new MyPredicate2[Int] {
    override def test(elem: Int): Boolean = elem % 2 == 0
  }).toString)

  println((listOfIntegers ++ anotherListOfIntegers).toString)

  println(listOfIntegers.flatMap(new MyTransformer2[Int, MyList2[Int]] {
    override def transform(elem: Int): MyList2[Int] = new Cons2(elem, new Cons2(elem + 1, Empty2))
  }).toString)

  println(cloneListOfIntegers == listOfIntegers)

}
