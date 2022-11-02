package scala

import scala.io.StdIn.readLine

case class Controller() {
  val drawPileO = new DrawPile

  def runLL = {
    val (playerList:List[Player],discardPiles:List[List[Int]]) = legeSpielerAn
    val drawPile = new DrawPile
    val (newDrawPile: List[Int], hands: List[Int]) =
      drawPile.startingHands(drawPile.newPile, playerList.length)
    getInputAndPrintLoop(-1, playerList, newDrawPile, hands, discardPiles)
  }
  def legeSpielerAn: (List[Player],List[List[Int]]) = {
    val playerList: List[Player] = rekLegeSpielerAn(Nil, spieleranzahl)
    val discardPiles: List[List[Int]] = rekLegeAblagestapelAn(Nil, playerList.length)
    (playerList,discardPiles)
  }
  def rekLegeSpielerAn(
      playerList: List[Player],
      playerAmount: Int
  ): List[Player] = {
    printf("Bitte Namen fuer Spieler %d angeben\n", playerList.length + 1)
    val input = readLine
    val updatedList = Player(input) :: playerList
    if (updatedList.length != playerAmount) {
      rekLegeSpielerAn(updatedList, playerAmount)
    } else {
      updatedList
    }
  }

  def rekLegeAblagestapelAn(
      discardList: List[List[Int]],
      playerAmount: Int
  ): List[List[Int]] = {
    val newTile: List[Int] = List(0)
    val updatedList = newTile :: discardList
    if (updatedList.length != playerAmount) {
      rekLegeAblagestapelAn(updatedList, playerAmount)
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

  def getInputAndPrintLoop(currentPlayer: Int, playerList: List[Player], drawPile: List[Int], hands: List[Int], discardPiles:List[List[Int]]): Unit = {

    val newCurrentPlayer = (currentPlayer + 1) % playerList.length
    val (newDrawPile:List[Int], drawedCard: Int) = drawPileO.drawAndGet(drawPile)
    printf("\n%s ist an der Reihe\n", playerList(newCurrentPlayer))
    print(Board(Vector(drawedCard, hands(newCurrentPlayer), discardPiles(newCurrentPlayer).head)))
    println("Welche Karte moechtest du spielen? (1|2)")
    val input = readLine
    input match
      case "1" =>
        val newDiscardPiles = playCard(newCurrentPlayer, drawedCard, discardPiles)
        getInputAndPrintLoop(newCurrentPlayer, playerList, newDrawPile, hands, newDiscardPiles)
      case "2" =>
        val newDiscardPiles = playCard(newCurrentPlayer, hands(newCurrentPlayer), discardPiles)
        val newHands = hands.updated(newCurrentPlayer, drawedCard)
        getInputAndPrintLoop(newCurrentPlayer, playerList, newDrawPile, newHands, newDiscardPiles)
      case "q"|"Q" => return
      case _ =>
        println("1 oder 2 einzugeben ist doch wirklich nicht schwierig oder?")
        getInputAndPrintLoop(currentPlayer, playerList, drawPile, hands, discardPiles)
  }

  def playCard(currentPlayer:Int, card:Int, discardPiles:List[List[Int]]):List[List[Int]] = {
    val newPlayerDiscardPile = card :: discardPiles(currentPlayer)
    discardPiles.updated(currentPlayer,newPlayerDiscardPile)
  }
}
