package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

//trait Command {
//
//  def apply(state: State): State
//}

trait Command extends (State => State) {
}

object Command {
  val MKDIR: String = "mkdir"
  val LS: String = "ls"
  val PWD: String = "pwd"
  val TOUCH: String = "touch"
  val CD: String = "cd"
  val RM: String = "rm"
  val ECHO: String = "echo"
  val CAT: String = "cat"

  def emptyCommand: Command = new Command {
    override def apply(state: State): State = state
  }

  def incompleteCommand(name: String): Command = new Command {
    override def apply(state: State): State =
      state.setMessage(name + ": incomplete command!")
  }

  def from(input: String): Command = {
    val tokens: Array[String] = input.split(" ")

    if (input.isEmpty || tokens.isEmpty) emptyCommand
    else tokens(0) match {
      case MKDIR =>
        if (tokens.length < 2) incompleteCommand(MKDIR)
        else new Mkdir(tokens(1))
      case LS =>
        new Ls
      case PWD =>
        new Pwd
      case TOUCH =>
        if (tokens.length < 2) incompleteCommand(MKDIR)
        else new Touch(tokens(1))
      case CD =>
        if (tokens.length < 2) incompleteCommand(CD)
        else new Cd(tokens(1))
      case RM =>
        if (tokens.length < 2) incompleteCommand(RM)
        else new Rm(tokens(1))
      case ECHO =>
        if (tokens.length < 2) incompleteCommand(ECHO)
        else new Echo(tokens.tail)
      case CAT =>
        if (tokens.length < 2) incompleteCommand(CAT)
        else new Cat(tokens(1))
      case _ =>
        new UnknownCommand
    }


//    if (MKDIR.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(MKDIR)
//      else new Mkdir(tokens(1))
//    } else if (LS.equals(tokens(0))) {
//      new Ls
//    } else if (PWD.equals(tokens(0))) {
//      new Pwd
//    } else if (TOUCH.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(MKDIR)
//      else new Touch(tokens(1))
//    } else if (CD.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(CD)
//      else new Cd(tokens(1))
//    } else if (RM.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(RM)
//      else new Rm(tokens(1))
//    } else if (ECHO.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(ECHO)
//      else new Echo(tokens.tail)
//    } else if (CAT.equals(tokens(0))) {
//      if (tokens.length < 2) incompleteCommand(CAT)
//      else new Cat(tokens(1))
//    }
//    else new UnknownCommand
  }
}
