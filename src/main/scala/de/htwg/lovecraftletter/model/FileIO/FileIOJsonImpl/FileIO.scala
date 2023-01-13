package de.htwg.lovecraftletter.model
package FileIO
package FileIOJsonImpl

//import scalafx.stage.FileChooser
//import scalafx.stage.FileChooser.ExtensionFilter
import javax.swing.JFileChooser
import java.io.File

class FileIO extends FileIOInterface {
  override def load(game: GameStateInterface): GameStateInterface = {
    //select safeGame File
    var chooser = new JFileChooser()
    chooser.setCurrentDirectory(new java.io.File("src/main/savegames/"))
    chooser.setDialogTitle("SaveGame laden")
    // Set shown file filter to JSON files only
    fileChooser.extensionFilters.addAll(
      new ExtensionFilter("JSON Files", "*.json")
    )
    val seletedFile = fileChooser.showOpenDialog(null)

    return game
  }

  override def save(game: GameStateInterface): Unit = {
    println("Spiel gespeichert")
  }
}
