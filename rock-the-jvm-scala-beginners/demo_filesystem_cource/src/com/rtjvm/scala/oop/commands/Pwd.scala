package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.filesystem.State

/**
  * Created by Daniel on 29-Oct-17.
  */
class Pwd extends Command {

  override def apply(state: State): State =
    state.setMessage(state.wd.path)
}
