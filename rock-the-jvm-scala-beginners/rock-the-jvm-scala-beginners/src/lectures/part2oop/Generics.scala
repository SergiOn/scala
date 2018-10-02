package lectures.part2oop

object Generics extends App {

  class MyList[+A] {
//  trait MyList[A] {

    // use the type A

//    def add(element: A): MyList[A] = ???

    def add[B >: A](element: B): MyList[B] = ???  // B supertype of A

  }

  class MyMap[Key, Value]

  val listOfIntegers = new MyList[Int]
  val listOfStrings = new MyList[String]


  // generics methods

  object MyList {
    def empty[A]: MyList[A] = ???
  }

  val myEmptyListOfIntegers = MyList.empty[Int]


  // variance problem

  class Animal

  class Cat extends Animal

  class Dog extends Animal

  // 1. yes, List[Cat] extends List[Animal] = COVARIANCE

  class CovariantList[+A]

  val animal: Animal = new Cat

  val animalList: CovariantList[Animal] = new CovariantList[Cat]

  // animalList.add(new Dog) ??? HARD QUESTION => we return a list of Animals

  // 2. NO = INVARIANCE

  class InvariantList[A]

//  val invariantAnimalList: InvariantList[Animal] = new InvariantList[Cat] // error
  val invariantAnimalList: InvariantList[Animal] = new InvariantList[Animal]

  // 3. Hell, no! CONTRAVARIANCE

  class ContravariantList[-A]

  val contravariantList: ContravariantList[Cat] = new ContravariantList[Animal]

  class Trainer[-A]

  val trainer: Trainer[Cat] = new Trainer[Animal]


  // bounded types

  class Cage[A <: Animal](animal: A)  // subtypes
//  class Cage[A >: Animal](animal: A)  // supertype

  val cage = new Cage(new Dog)
//  val cage: Cage[Dog] = new Cage[Dog](new Dog)

//  class Car
  // generic type needs proper bounded type
//  val newCage = new Cage(new Car) // error

}
