package test

object Main extends App {

  // directory: ./files/test (should be absolute path)
  // tested: "to be or not to be" => file1.txt : 100%; file2.txt : 66%;
  // tested: "look" => file1.txt : 0%; file2.txt : 100%;

  Program().process(args)

}
