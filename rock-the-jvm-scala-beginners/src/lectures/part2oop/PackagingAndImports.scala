package lectures.part2oop

//import playground._
//import playground.{PrinceCharming, Cinderella}
import java.util.Date
import java.sql.{Date => SqlDate}

import playground.{PrinceCharming, Cinderella => Princess}

object PackagingAndImports extends App {

  // package members are accessible by their simple name

  val writer = new Writer("Daniel", "RockTheJVM", 2018)


  // import the package

//  val princess = new playground.Cinderella  // fully qualified name
//  val princess = new Cinderella
  val princess = new Princess


  // packages are in hierarchy

  // matching folder structure


  // package object

  sayHello
  println(SPEED_OF_LIGHT)


  // imports

  val prince = new PrinceCharming

  val date = new Date
  val sqlDate = new SqlDate(2018, 5,4)


  // defaults imports

  // java.lang - String, Object, Exception
  // scala - Int, Nothing, Functions
  // scala.Predef - println, ???

}
