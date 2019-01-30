package exercises

import scala.annotation.tailrec

trait MySet[A] extends (A => Boolean) {

  /*
    EXERCISE - implement a functional set
   */

  def apply(elem: A): Boolean =
    contains(elem)

  def contains(elem: A): Boolean
  def +(elem: A): MySet[A]
  def ++(anotherSet: MySet[A]): MySet[A]

  def map[B](f: A => B): MySet[B]
  def flatMap[B](f: A => MySet[B]): MySet[B] // MySet[(A,B)]
  def filter(predicate: A => Boolean): MySet[A]
  def foreach(f: A => Unit): Unit

  /*
   EXERCISE #2
   - removing an element
   - intersection with another set
   - difference with another set
  */
  def -(elem: A): MySet[A]
  def --(anotherSet: MySet[A]): MySet[A] // difference
  def &(anotherSet: MySet[A]): MySet[A]  // intersection

  // EXERCISE #3 - implement a unary_! = NEGATION of a set
  // set[1,2,3] =>
  def unary_! : MySet[A]
}

class EmptySet[A] extends MySet[A] {
  override def contains(elem: A): Boolean = false
  override def +(elem: A): MySet[A] = new NonEmptySet[A](elem, this)
  override def ++(anotherSet: MySet[A]): MySet[A] = anotherSet

  override def map[B](f: A => B): MySet[B] = new EmptySet[B]
  override def flatMap[B](f: A => MySet[B]): MySet[B] = new EmptySet[B]
  override def filter(predicate: A => Boolean): MySet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  // part 2
  override def -(elem: A): MySet[A] = this
  override def --(anotherSet: MySet[A]): MySet[A] = this
  override def &(anotherSet: MySet[A]): MySet[A] = this

  override def unary_! : MySet[A] = new AllInclusiveSet[A]
}

//class AllInclusiveSet[A] extends MySet[A] {
//  override def contains(elem: A): Boolean = true
//  override def +(elem: A): MySet[A] = this
//  override def ++(anotherSet: MySet[A]): MySet[A] = this
//
//  // naturals = AllInclusiveSet[Int] = all naturals number
//  // naturals.map(x => x % 3) => ???
//  // [0, 1, 2]
//  override def map[B](f: A => B): MySet[B] = ???
//  override def flatMap[B](f: A => MySet[B]): MySet[B] = ???
//
//  override def filter(predicate: A => Boolean): MySet[A] = ??? // property-base set
//  override def foreach(f: A => Unit): Unit = ???
//  override def -(elem: A): MySet[A] = ???
//  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)
//  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet)
//
//  override def unary_! : MySet[A] = new EmptySet[A]
//}

class PropertyBasedSet[A](propert: A => Boolean) extends MySet[A] {

}

class NonEmptySet[A](head: A, tail: MySet[A]) extends MySet[A] {
  override def contains(elem: A): Boolean =
    elem == head || tail.contains(elem)

  override def +(elem: A): MySet[A] =
    if (this contains elem) this
    else new NonEmptySet[A](elem, this)

  /*
    [1 2 3] ++ [4 5] =
    [2 3] ++ [4 5] + 1 =
    [3] ++ [4 5] + 1 + 2 =
    [] ++ [4 5] + 1 + 2 + 3
    [4 5] + 1 + 2 + 3 = [4 5 1 2 3]
   */
  override def ++(anotherSet: MySet[A]): MySet[A] =
    tail ++ anotherSet + head

  override def map[B](f: A => B): MySet[B] = (tail map f) + f(head)
  override def flatMap[B](f: A => MySet[B]): MySet[B] = (tail flatMap f) ++ f(head)
  override def filter(predicate: A => Boolean): MySet[A] = {
    val filteredTail = tail filter predicate
    if (predicate(head)) filteredTail + head
    else filteredTail
  }
  override def foreach(f: A => Unit): Unit = {
    tail foreach f
    f(head)
//    tail foreach f
  }

  // part 2
  override def -(elem: A): MySet[A] =
    if (head == elem) tail
    else tail - elem + head

//  override def --(anotherSet: MySet[A]): MySet[A] =
//    if (anotherSet contains head) tail
//    else tail -- anotherSet

//  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet.contains(x))
//  override def --(anotherSet: MySet[A]): MySet[A] = filter(x => !anotherSet(x))
  override def --(anotherSet: MySet[A]): MySet[A] = filter(!anotherSet)

//  override def &(anotherSet: MySet[A]): MySet[A] = filter(x => anotherSet.contains(x))
//  override def &(anotherSet: MySet[A]): MySet[A] = filter(x => anotherSet(x))
  override def &(anotherSet: MySet[A]): MySet[A] = filter(anotherSet) // intersection = filtering!

  override def unary_! : MySet[A] = ???
}

object MySet {
  /*
    val s = MySet(1,2,3) = buildSet(seq(1,2,3), [])
    = buildSet(seq(2,3), [] + 1)
    = buildSet(seq(3), [1] + 2)
    = buildSet(seq(), [1, 2] + 3)
    = [1,2,3]
   */
  def apply[A](values: A*): MySet[A] = {
    @tailrec
    def buildSeq(valSeq: Seq[A], acc: MySet[A]): MySet[A] =
      if (valSeq.isEmpty) acc
      else buildSeq(valSeq.tail, acc + valSeq.head)

    buildSeq(values.toSeq, new EmptySet[A])
  }
}

object MySetPlayground extends App {
  val s = MySet(1,2,3,4)
  s foreach println

  println("---|---")
  s + 5 foreach println

  println("---|---")
  s ++ MySet(8, 9) foreach println

  println("---|---")
  s + 5 ++ MySet(-1, -2) + 3 map (x => x * 10) foreach println

  println("---|---")
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, 10 * x)) foreach println

  println("---|---")
  s + 5 ++ MySet(-1, -2) + 3 flatMap (x => MySet(x, 10 * x)) filter (_ % 2 == 0) foreach println
}
