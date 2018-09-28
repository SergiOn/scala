package lectures.part2oop

object OOBasics extends App {

  val person = new Person("John", 26)
//  val person = new Person()
  println(person)
  println(person.age)
  println(person.x)

  person.greet("Daniel")
  person.greet()

}

//class Person(name: String, val age: Int)

//class Person(name: String, val age: Int) {

class Person(name: String, val age: Int = 0) {
  // body
  val x = 2

  println(1 + 3)

  // method
  def greet(name: String): Unit = println(s"${this.name} says: Hi, $name")

  // overloading
  def greet(): Unit = println(s"Hi, I am $name")
//  def greet(): Unit = println(s"Hi, I am ${this.name}")

  // multiple constructors
  def this(name: String) = this(name, 0)
  def this() = this("John Doe")

}
