package de.htwg.lovecraftletter.model
package FileIO
package FileIOImpl

import javax.swing.JFileChooser
import java.io._
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import de.htwg.lovecraftletter.model.GameStateImpl._
import de.htwg.lovecraftletter.model.PlayerImpl._
import play.api.libs.json.*
import scala.io.Source

class FileIOJSON extends FileIOInterface{
  override def load(oldGameState: GameStateInterface): GameStateInterface = {
    //select safeGame File
    var chooser = new JFileChooser()
    chooser.setCurrentDirectory(new java.io.File("src\\savegames\\"))
    chooser.setDialogTitle("SaveGame laden")
    // Set shown file filter to JSON files only
    //chooser.extensionFilters.addAll(
    //  new ExtensionFilter("JSON Files", "*.json")
    //)
    chooser.showOpenDialog(null)
    val selectedFile = chooser.getSelectedFile().getAbsolutePath()
    if (selectedFile == null) {
        return oldGameState
    }
    val source: String = Source.fromFile(selectedFile).getLines.mkString
    //val source = scala.io.Source.fromFile(selectedFile)
    //val xml = XML.loadString(source.mkString)
    val json: JsValue = Json.parse(source)
    val currentPlayer = json("currentPlayer").as[Int]
    val drawPile: List[Int] = (json \ "drawPile").as[List[Int]]
    val player: List[Player] = loadPlayer(json)
    val currentCard:Int = (json \ "currentCard").as[Int]
    val gameState = GameState(currentPlayer, drawPile, player, currentCard)
    gameState
  }

  def loadPlayer(json:JsValue): List[Player] = {
    val player = (json \ "player").as[List[JsValue]]
    var playerList: List[Player] = Nil
    for (i <- 0 until player.length) {
        val name = (player(i) \ "name").as[String]
        val hand: Int = (player(i) \ "hand").as[Int]
        val discardPile: List[Int] = (player(i)  \ "discardPile").as[List[Int]]
        val inGame:Boolean = (player(i) \ "inGame").as[Boolean]
        val p = Player(name, hand, discardPile, inGame)
        playerList = p :: playerList
    }
    playerList
  }



  override def save(gameState: GameStateInterface): Unit = {
    val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
    val path_and_name = "src\\savegames\\gamesave"
    val pw = new PrintWriter(new File(path_and_name + time + ".json"))
    pw.write(gameStateToJSON(gameState))
    pw.close()
  }

  def gameStateToJSON(gameState: GameStateInterface): String = {
    Json.obj(
        "currentPlayer" -> gameState.currentPlayer,
        "drawPile" -> Json.toJson(gameState.drawPile),
        "player" -> Json.toJson(
            for (x <- 0 until gameState.player.length) yield {
                Json.obj(
                "name" -> gameState.player(x).name,
                "hand" -> gameState.player(x).hand,
                "discardPile" -> Json.toJson(gameState.player(x).discardPile),
                "inGame" -> gameState.player(x).inGame
                )
            }
        ),
        "currentCard" -> gameState.currentCard
    ).toString
  }
}
