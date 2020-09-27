package test

import java.io.File

import org.scalatest.{BeforeAndAfterEach, EitherValues, PrivateMethodTester}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ProgramSpec extends AnyFlatSpec with Matchers with BeforeAndAfterEach with PrivateMethodTester with EitherValues {
  import test.Program._

  var sut: Program = _

  override def beforeEach(): Unit = {
    sut = new Program()
  }

  it should "has default top maximum matching filenames property" in {
    Program.topMaximumMatchingFilenames should be (10)
  }

  "getPath" should "get path error" in {
    val getPath = PrivateMethod[Either[FilePathError, String]](Symbol("getPath"))
    val args = Array[String]()
    val Left(value: FilePathError) = sut.invokePrivate(getPath(args))

    value should be (MissingPathArg)
  }

  "getPath" should "get correct path" in {
    val getPath = PrivateMethod[Either[FilePathError, String]](Symbol("getPath"))
    val args = Array[String]("/path", "/second-path")
    val Right(value: String) = sut.invokePrivate(getPath(args))

    value should be ("/path")
  }

  "readFile" should "get 'NotDirectory' error" in {
    val readFile = PrivateMethod[Either[ReadFileError, java.io.File]](Symbol("readFile"))
    val path = "./path"
    val Left(value: ReadFileError) = sut.invokePrivate(readFile(path))

    value should be (NotDirectory("Path [./path] is not a directory"))
  }

  "readFile" should "get correct file" in {
    val readFile = PrivateMethod[Either[ReadFileError, java.io.File]](Symbol("readFile"))
    val path: String = java.nio.file.Paths.get("files/test").toAbsolutePath.toString
    val Right(value: File) = sut.invokePrivate(readFile(path))

    value.list().length should be (2)
  }

  "index" should "get correct index" in {
    val index = PrivateMethod[Index](Symbol("index"))
    val directory: File = java.nio.file.Paths.get("files/test").toAbsolutePath.toFile
    val value: Index = sut.invokePrivate(index(directory))

    val expected = Index(List(
      IndexFile("file1.txt", Set("to", "be", "or", "not", "hello", "hi", "file1")),
      IndexFile("file2.txt", Set("to", "bear", "or", "not", "hello", "hi", "look", "file2"))
    ))

    value should equal (expected)
  }

}
