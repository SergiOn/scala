package exercises

abstract class MyList1 {

  /*
    head = first element of the list
    tail = remainder of the list
    isEmpty = is this list Empty1
    add(int) => new list with this element added
    toString => a string representation of the list
  */

  def head: Int
  def tail: MyList1
  def isEmpty: Boolean
  def add(element: Int): MyList1
  def printElements: String
  // polymorphic call
  override def toString: String = "[" + printElements + "]"

}

object Empty1 extends MyList1 {
  def head: Int = throw new NoSuchElementException
  def tail: MyList1 = throw new NoSuchElementException
  def isEmpty: Boolean = true
  def add(element: Int): MyList1 = new Cons1(element, Empty1)
  def printElements: String = ""
}

class Cons1(h: Int, t: MyList1) extends MyList1 {
  def head: Int = h
  def tail: MyList1 = t
  def isEmpty: Boolean = false
  def add(element: Int): MyList1 = new Cons1(element, this)
  def printElements: String =
    if (t.isEmpty) "" + h
    else h + " " + t.printElements
}


object ListTest1 extends App {

//  val list = new Cons1(1, Empty1)
  val list = new Cons1(1, new Cons1(2, new Cons1(3, Empty1)))
  println(list.head)
  println(list.tail.head)
  println(list.add(4).head)
  println(list.isEmpty)

  // polymorphic call
  println(list.toString)

}
