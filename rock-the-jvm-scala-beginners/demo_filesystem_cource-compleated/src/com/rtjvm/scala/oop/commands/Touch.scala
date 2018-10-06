package com.rtjvm.scala.oop.commands

import com.rtjvm.scala.oop.files.{File, DirEntry}
import com.rtjvm.scala.oop.filesystem.State

/**
  * Created by Daniel on 29-Oct-17.
  */
class Touch(name: String) extends CreateEntry(name) {

  override def createSpecificEntry(state: State): DirEntry =
    File.empty(state.wd.path, name)
}
