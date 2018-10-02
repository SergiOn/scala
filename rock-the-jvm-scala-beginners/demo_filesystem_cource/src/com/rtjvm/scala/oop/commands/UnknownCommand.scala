package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

/**
  * Created by Daniel on 28-Oct-17.
  */
class UnknownCommand extends Command {

  override def apply(state: State): State =
    state.setMessage("Command not found!")
}
