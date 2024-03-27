package de.htwg.lovecraftletter.model
package FileIO
package FileIOImpl

import javax.swing.JFileChooser
import java.io.*
import scala.xml.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import de.htwg.lovecraftletter.model.GameStateImpl.*
import de.htwg.lovecraftletter.model.PlayerImpl.*

import java.awt.Component

class FileIOXML extends FileIOInterface{
  override def load(oldGameState: GameStateInterface): GameStateInterface = {
    //select safeGame File
    val chooser = new JFileChooser()
    chooser.setCurrentDirectory(new java.io.File("src\\savegames\\"))
    chooser.setDialogTitle("SaveGame laden")
    // Set shown file filter to JSON files only
    //chooser.extensionFilters.addAll(
    //  new ExtensionFilter("JSON Files", "*.json")
    //)
    val parentWindowOption: Option[Component] = None
    chooser.showOpenDialog(parentWindowOption.orNull)
    val selectedFileOption = Option(chooser.getSelectedFile())
    if (selectedFileOption.isEmpty) {
        return oldGameState
    }
    val selectedFile = selectedFileOption.get.getAbsolutePath()
    val source = scala.io.Source.fromFile(selectedFile)
    val xml = XML.loadString(source.mkString)
    val currentPlayer = (xml \ "currentPlayer").text.toInt
    val drawPile: List[Int] = (xml \ "drawPile").text.trim.split("t").map(_.toInt).toList
    val player: List[Player] = loadPlayer(xml)
    val currentCard = (xml \ "currentCard").text.toInt
    val gameState = GameState(currentPlayer, drawPile, player, currentCard)
    gameState
  }

  def loadPlayer(xml: Node): List[Player] = {
    val names = xml \\ "gameState" \\ "player" \\ "value" \\ "name" flatMap(_.child)
    val hands = xml \\ "gameState" \\ "player" \\ "value" \\ "hand" flatMap(_.child)
    val discardPiles = xml \\ "gameState" \\ "player" \\ "value" \\ "discardPile" flatMap(_.child)
    val inGames = xml \\ "gameState" \\ "player" \\ "value" \\ "inGame" flatMap(_.child)
  
    def loop(i: Int, acc: List[Player]): List[Player] = {
      if (i >= names.length) acc
      else {
        val player = Player(names(i).text.trim, hands(i).text.trim.toInt, getDiscardPile(discardPiles(i)), inGames(i).text.trim.toBoolean)
        loop(i + 1, player :: acc)
      }
    }
  
    loop(0, Nil).reverse
  }

  def getDiscardPile(dcp: Node): List[Int] = {
    dcp.text.trim.split("t").map(_.toInt).toList
  }

  override def save(gameState: GameStateInterface): Unit = {
    val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val path_and_name = "src\\savegames\\gamesave"
    //val path_and_name = "gamesave"
    val pw = new PrintWriter(new File(path_and_name + time + ".xml"))
    pw.write(gameStateToXML(gameState))
    //pw.write("gameStateToXML(gameState).toString()")
    pw.close()
  }

  def gameStateToXML(gameState: GameStateInterface): String = {
    return (<gameState>
        <currentPlayer>{gameState.currentPlayer}</currentPlayer>
        <drawPile>
            { for (x <- 0 until gameState.drawPile.length)
                yield {gameState.drawPile(x) + "t"}}
        </drawPile>
        <amountPlayer>{gameState.player.length}</amountPlayer>
        <player>
            { for (x <- 0 until gameState.player.length)
                yield playerToXML(gameState.player(x)) }
        </player>
        <currentCard>{gameState.currentCard}</currentCard>
    </gameState>).toString()
  }

  def playerToXML(player: PlayerInterface) = {
    <value>
        <name>{player.name}</name>
        <hand>{player.hand}</hand>
        <discardPile>
            { for (x <- 0 until player.discardPile.length)
                yield { player.discardPile(x) + "t"}}
        </discardPile>
        <inGame>{player.inGame}</inGame>
    </value>
  }
}
