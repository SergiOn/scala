package exercises

abstract class MyList2[+A] {

  /*
    head = first element of the list
    tail = remainder of the list
    isEmpty = is this list empty
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

}

object Empty2 extends MyList2[Nothing] {
  def head: Nothing = throw new NoSuchElementException
  def tail: MyList2[Nothing] = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add[B >: Nothing](element: B): MyList2[B] = new Cons2(element, Empty2)
  def printElements: String = ""
}

class Cons2[+A](h: A, t: MyList2[A]) extends MyList2[A] {
  def head: A = h
  def tail: MyList2[A] = t
  def isEmpty: Boolean = false
  def add[B >: A](element: B): MyList2[B] = new Cons2(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements
}


object ListTest2 extends App {

//  val listOfIntegers: MyList2[Int] = Empty2
//  val listOfStrings: MyList2[String] = Empty2

  val listOfIntegers: MyList2[Int] = new Cons2(1, new Cons2(2, new Cons2(3, Empty2)))
  val listOfStrings: MyList2[String] = new Cons2("Hello", new Cons2("Scala", Empty2))

  println(listOfIntegers.toString)
  println(listOfStrings.toString)


//  //  val list = new Cons2(1, Empty2)
//  val list = new Cons2(1, new Cons2(2, new Cons2(3, Empty2)))
//  println(list.head)
//  println(list.tail.head)
//  println(list.add(4).head)
//  println(list.isEmpty)
//
//  // polymorphic call
//  println(list.toString)

}
