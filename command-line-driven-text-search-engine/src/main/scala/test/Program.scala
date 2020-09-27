package test

import scala.io.Source
import scala.util.{Try, Using}
import Function.tupled
import scala.io.StdIn.readLine

object Program {
  val topMaximumMatchingFilenames = 10

  case class IndexFile(name: String, words: Set[String])
  case class Index(files: List[IndexFile])

  sealed trait FilePathError
  case object MissingPathArg extends FilePathError

  sealed trait ReadFileError
  case class FileNotFound(exception: Throwable) extends ReadFileError
  case class NotDirectory(error: String) extends ReadFileError

  def apply(): Program = new Program()
}

class Program {
  import test.Program._

  def process(args: Array[String]): Unit = {
    getPath(args)
      .flatMap(readFile)
      .fold(
        println,
        file => iterate(index(file))
      )
  }

  def getPath(args: Array[String]): Either[FilePathError, String] = {
    args.headOption
      .toRight(MissingPathArg)
  }

  def readFile(path: String): Either[ReadFileError, java.io.File] = {
    Try(new java.io.File(path))
      .fold(
        throwable => Left(FileNotFound(throwable)),
        file => {
          if (file.isDirectory) Right(file)
          else Left(NotDirectory(s"Path [$path] is not a directory"))
        }
      )
  }

  def index(directory: java.io.File): Index = {
    val files: List[java.io.File] = directory.listFiles()
      .toList
      .filter(_.isFile)

    val pathWithLines: List[(String, List[String])] = files.map(file => {
      val absolutePath: String = file.getAbsolutePath
      val name: String = file.getName
      val lines: List[String] = Using(Source.fromFile(absolutePath))(_.getLines().toList).getOrElse(List())
      (name, lines)
    })

    val indexFiles: List[IndexFile] = pathWithLines.map(tupled((name, lines) => {
      val words: Set[String] = lines.flatMap(_.split(" ").map(_.trim)).toSet
      IndexFile(name, words)
    }))

    Index(indexFiles)
  }

  def iterate(indexedFiles: Index): Unit = {
    print(s"search> ")
    val searchString: String = readLine()

    val wordsToSearch: Array[String] = searchString.split(" ")
      .map(_.trim)

    val result: String = indexedFiles.files
      .map { case IndexFile(name, words) =>
        val count: Int = wordsToSearch.count(words.contains)
        val calc = count * 100 / wordsToSearch.length
        (name, calc)
      }
      .sortBy(tupled((name, calc) => (-calc, name)))
      .take(topMaximumMatchingFilenames)
      .foldRight("")((accumulator, word) => {
        val (name, calc) = accumulator
        s"$name : $calc%; $word"
      })

    println(result)

    iterate(indexedFiles)
  }

}
