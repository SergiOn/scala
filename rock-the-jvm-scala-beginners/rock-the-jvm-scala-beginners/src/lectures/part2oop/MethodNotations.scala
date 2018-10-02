package lectures.part2oop

import scala.language.postfixOps

object MethodNotations extends App {

  class Person(val name: String, favoriteMovie: String, val age: Int = 0) {
    def likes(movie: String): Boolean = movie == favoriteMovie
    def hangOutWith(person: Person): String = s"${this.name} is hanging out with ${person.name}"
    def +(person: Person): String = s"${this.name} is hanging out with ${person.name}"
    def +(nickname: String): Person = new Person(s"$name ($nickname)", favoriteMovie)
    def unary_! : String = s"$name, what the heck?!"
    def unary_+ : Person = new Person(name, favoriteMovie, age + 1)
    def isAlive: Boolean = true
    def apply(): String = s"Hi, my name is $name and I like $favoriteMovie"
    def apply(n: Int): String = s"$name watched $favoriteMovie $n times"
    def learns(thing: String): String = s"$name is learning $thing"
    def learnsScala: String = this learns "Scala"
  }

  val mary = new Person("Mary", "Inception")
  println(mary.likes("Inception"))
  println(mary likes "Inception") // equivalent
  // infix notation = operator notation (syntactics sugar)

  // "operators" in scala

  val tom = new Person("Tom", "Fight Club")
  println(mary hangOutWith tom)
  println(mary + tom)
  println(mary.+(tom))

  println(1 + 2)
  println(1.+(2))

  // ALL OPERATORS ARE METHODS
  // Akka actors have ! ?

  // prefix notation

  val x = -1
  val y = 1.unary_-

  println(!mary)
  println(mary.unary_!)

  // postfix notation

  println(mary.isAlive)
  println(mary isAlive)

  // apply

  println(mary.apply())
  println(mary()) // equivalent


  println(mary + "the Rockstar")
  println((mary + "the Rockstar")())
  println((mary + "the Rockstar").apply())

  println(mary.age)
  println((+mary).age)

  println(mary learnsScala)

  println(mary(10))

}
