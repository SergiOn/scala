package lectures.part2oop

object CaseClasses extends App {

  /*
    equals, hashCode, toString
  */

  case class Person(name: String, age: Int)
//  class Person(name: String, age: Int) // error, modifier

  // 1. class parameters are fields

  val jim = new Person("Jim", 34)

  println(jim.name)

  // 2. sensible to string

  // println(instance) = println(instance.toString)  // syntactic sugar

  println(jim.toString)
  println(jim)

  // 3.  equals and hashCode implemented OOTB (out of the box)

  val jim2 = new Person("Jim", 34)

  println(jim == jim2)  // true

  // 4. CCS (case classes) have handy copy method

  val jim3 = jim.copy(age = 45)

  println(jim3)

  // 5. CCS (case classes) have companion objects

  val thePerson = Person

  val mary = Person("Mary", 23)

  // 6. CCs are serializable

  // Akka

  // 7. CCs have extractor patterns = CCs can be used in PATTERN MATCHING

  case object UnitedKingdom {

    def name: String = "The UK of GB and NI"

  }

}
