package lectures.part2oop

object Objects extends App {
//object Objects {

  // SCALA DOES NOT HAVE CLASS-LEVEL FUNCTIONALITY ("static")

  object Person { // type + its only instance

    // "static"/"class" - level functionality

    val N_EYES = 2

    def canFly: Boolean = false

    // factory method
//    def from(mother: Person, father: Person): Person = new Person("Bobbie")
    def apply(mother: Person, father: Person): Person = new Person("Bobbie")

  }

//  class Person {

  class Person(val name: String) {

    // instance-level functionality

  }
  // COMPANIONS

//  def main(args: Array[String]): Unit = {}

  println(Person.N_EYES)
  println(Person.canFly)

  // Scala object = SINGLETON INSTANCE

  val person1 = Person
  val person2 = Person

  println(person1 == person2)

  val mary = new Person("Mary")
  val john = new Person("John")

  println(mary == john)


//  val bobbie = Person.from(mary, john)
//  val bobbie = Person.apply(mary, john)
  val bobbie = Person(mary, john)


  // Scala Applications = Scala object with
  // def main(args: Array[String]): Unit

}
