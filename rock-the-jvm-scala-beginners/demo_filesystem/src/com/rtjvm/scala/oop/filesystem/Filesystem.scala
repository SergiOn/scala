package com.rtjvm.scala.oop.filesystem

import java.util.Scanner

import com.rtjvm.scala.oop.commands.Command
import com.rtjvm.scala.oop.files.Directory

object Filesystem extends App {

  val root: Directory = Directory.ROOT
  var state: State = State(root, root)
  val scanner: Scanner = new Scanner(System.in)

  while(true) {
    state.show
    val input: String = scanner.nextLine()
    state = Command.from(input).apply(state)
  }

}
