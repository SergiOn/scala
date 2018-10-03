package com.rtjvm.scala.oop.commands
import com.rtjvm.scala.oop.files.{DirEntry, Directory}
import com.rtjvm.scala.oop.filesystem.State

class Mkdir(name: String) extends Command {

  override def apply(state: State): State = {
    val wd: Directory = state.wd
    if (wd.hasEntry(name)) {
      state.setMessage("Entry " + name + " already exist!")
    } else if (name.contains(Directory.SEPARATOR)) {
      // mkdir something/somethingElse
      state.setMessage(name + " must not contain separator!")
    } else if(checkIllegal(name)) {
      state.setMessage(name + ": illegal entry name!")
    } else {
      doMkdir(state, name)
    }
  }

  def checkIllegal(name: String): Boolean = {
    name.contains(".")
  }

  def doMkdir(state: State, name: String): State = {
    def updateStructure(currentDirectory: Directory, path: List[String], newEntry: DirEntry): Directory = {
      /*
        someDir
          /a
          /b
          (new) /d

        => new someDir
          /a
            /...
          /b
            /...
          /d

        /a/b
          /c
          /d
          (new) /e

        new /a
          new /b (parent /a)
            /c
            /d
      */

      if (path.isEmpty) currentDirectory.addEntry(newEntry)
      else {
        /*
          /a/b
            /c
            /d
            (new entry)
          currentDirectory = /a
          path = ["b"]
        */
        val oldEntry: Directory = currentDirectory.findEntry(path.head).asDirectory
        currentDirectory.replaceEntry(oldEntry.name, updateStructure(oldEntry, path.tail, newEntry))
      }

      /*
        /a/b
          (contents)
          (new entry) /e

        updateStructure(root, ["a", "b"], "/e")
          => path.isEmpty?
          => oldEntry = /a
          root.replaceEntry("a", updateStructure(/a, ["b"], /e))
            => path.isEmpty?
            => oldEntry = /b
            /a.replaceEntry("b", updateStructure(/b, [], /e))
              => path.isEmpty? => /b.add(/e)

        newRoot = updateStructure(root, ["a", "b"], "/e") = root.replaceEntry("a", updateStructure(/a, ["b"], /e)) = /a.replaceEntry("b", updateStructure(/b, [], /e)) = /b.add(/e)
          => path.isEmpty?
          => oldEntry = /a
          root.replaceEntry("a", updateStructure(/a, ["b"], /e)) = /a.replaceEntry("b", updateStructure(/b, [], /e)) = /b.add(/e)
            => path.isEmpty?
            => oldEntry = /b
            /a.replaceEntry("b", updateStructure(/b, [], /e)) = /b.add(/e)
              => path.isEmpty? => /b.add(/e)
      */
    }

    val wd: Directory = state.wd

    // 1. all the directories in the full path
    val allDirsInPath = wd.getAllFoldersInPath

    // 2. create new directory entry in the wd
    val newDir: Directory = Directory.empty(wd.path, name)

    // 3. update the whole directory structure starting from root
    // (the directory structure is IMMUTABLE)
    val newRoot: Directory = updateStructure(state.root, allDirsInPath, newDir)

    // 4. find new working directory INSTANCE given wd's full path, in the NEW directory structure
    val newWd: Directory = newRoot.findDescendant(allDirsInPath)

    State(newRoot, newWd)
  }
}
