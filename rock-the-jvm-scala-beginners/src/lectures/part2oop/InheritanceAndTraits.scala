package lectures.part2oop

object InheritanceAndTraits extends App {

  // single class inheritance

//  class Animal {
//    def eat: Unit = println("nomnom")
//  }

//  final class Animal {
//  sealed class Animal {
  class Animal {

    val creatureType: String = "wild"

//    protected def eat: Unit = println("nomnom")
      def eat: Unit = println("nomnom")
//      final def eat: Unit = println("nomnom")

  }

//  class Cat extends Animal

  class Cat extends Animal {
    def crunch: Unit = {
      eat
      println("crunch crunch")
    }
  }

  val cat = new Cat
//  cat.eat
  cat.crunch


  // constructors

  class Person(name: String, age: Int) {
    def this(name: String) = this(name, 0)
  }

//  class Adult(name: String, age: Int, idCart: String) extends Person(name, age)

  class Adult(name: String, age: Int, idCart: String) extends Person(name)


  // overriding

//  class Dog extends Animal {
//    override val creatureType: String = "domestic"
//    override def eat: Unit = println("crunch, crunch")
//  }

  class Dog(override val creatureType: String) extends Animal {
    override def eat: Unit = {
      super.eat
      println("crunch, crunch")
    }
  }

//  class Dog(dogType: String) extends Animal {
//    override val creatureType: String = dogType
//  }

//  val dog = new Dog
  val dog = new Dog("K9")
  dog.eat
  println(dog.creatureType)


  // type substitution (broad: polymorphism)

  val unknownAnimal: Animal = new Dog("K9")
  unknownAnimal.eat


  // overRIDING vs overLOADING


  // super


  // preventing overrides

  // 1 - use final on member
  // 2 - use final on the entire class
  // 3 - seal the class = extends classes in THIS FILE, prevent extension in other files


}
