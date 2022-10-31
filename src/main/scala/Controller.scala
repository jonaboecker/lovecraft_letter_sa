package scala

import scala.io.StdIn.readLine

case class Controller() {
  def runLL = {
    val playerList = legeSpielerAn
    val drawPile = new DrawPile
    val (newDrawPile: List[Int], hands: List[Int]) =
      drawPile.startingHands(drawPile.newPile, playerList.length)
  }
  def legeSpielerAn: List[Player] = {
    val playerList: List[Player] = rekLegeSpielerAn(Nil, spieleranzahl)
    playerList
  }
  def rekLegeSpielerAn(
      playerList: List[Player],
      playerAmount: Int
  ): List[Player] = {
    printf("Bitte Namen f\u0000r Spieler %d angeben\n", playerList.length + 1)
    val input = readLine
    val updatedList = Player(input) :: playerList
    if (updatedList.length != playerAmount) {
      rekLegeSpielerAn(updatedList, playerAmount)
    } else {
      updatedList
    }
  }
  def spieleranzahl: Int = {
    println("Bitte Spieleranzahl zwischen 3 und 6 angeben.")
    val input = readLine
    input match
      case "3" => 3
      case "4" => 4
      case "5" => 5
      case "6" => 6
      case _   => spieleranzahl
  }
//   def getInputAndPrintLoop(currentPlayer: Int, playerList: List[Player]): Unit =
//     val newCurrentPlayer = (currentPlayer + 1) % playerList.length
//     print(Board(Vector()))
//     val input = readLine
//     parseInput(input) match
//       case None => field
//       case Some(newfield) =>
//         println(newfield)
//         getInputAndPrintLoop(newfield)

}
