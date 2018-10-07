package lectures.part3fp

object TuplesAndMaps extends App {

  // tuples = finite ordered "lists"

//  val aTuple = new Tuple2(2, "hello, Scala")  // Tuple2[Int, String] = (Int, String)
//  val aTuple = Tuple2(2, "hello, Scala")  // Tuple2[Int, String] = (Int, String)
  val aTuple = (2, "hello, Scala")  // Tuple2[Int, String] = (Int, String)

  println(aTuple)
  println(aTuple._1)  // 2
  println(aTuple.copy(_2 = "goodbye Java"))
  println(aTuple.swap)  // ("hello, Scala", 2)

  // Maps - keys -> values

  val aMap: Map[String, Int] = Map()

//  val phonebook = Map(("Jim", 555), ("Daniel", 789))
//  val phonebook = Map(("Jim", 555), "Daniel" -> 789)
  val phonebook = Map(("Jim", 555), "Daniel" -> 789).withDefaultValue(-1)
  // a -> b is sugar for (a, b)

  println(phonebook)

  // map ops

  println(phonebook.contains("Jim"))  // true
  println(phonebook("Jim"))  // 555
  println(phonebook("Mary")) // Exception or withDefaultValue

  // add a pairing

  val newPairing = "Mary" -> 678
  val newPhonebook = phonebook + newPairing

  println(newPhonebook)

  // functionals on Map
  // map, flatMap, filter

  println(phonebook.map(pair => pair._1.toLowerCase -> pair._2))
//  println(phonebook.map(pair => pair._1.toLowerCase))  // List

  // filter keys

//  println(phonebook.filterKeys(_.startsWith("J")))
  println(phonebook.filterKeys(x => x.startsWith("J")))

  // mapValues

//  println(phonebook.mapValues(number => number * 10))
  println(phonebook.mapValues(number => "0245-" + number))

  // conversions to other collections

  println(phonebook.toList)

  println(List(("Daniel", 555)).toMap)

  val names = List("Bob", "James", "Angela", "Mary", "Daniel", "Jim")

  println(names.groupBy(name => name.charAt(0)))
  // Map(J -> List(James, Jim), A -> List(Angela), M -> List(Mary), B -> List(Bob), D -> List(Daniel))

}
