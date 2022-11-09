package de.htwg.lovecraftletter
package aview

import controller._
import util.Observer
import model._

import scala.io.StdIn.readLine

final case class TUI(controller: Controller) extends Observer {
  controller.add(this)

  def runLL = {
    val playerList: List[Player] = createPlayers
    controller.initialize(playerList)
    show("Viel Spass beim spielen")
    getInputAndPrintLoop
  }

  def createPlayers: List[Player] = {
    show("Bitte Spieleranzahl zwischen 3 und 6 angeben")
    val playerList: List[Player] =
      rekCreatePlayers(Nil, controller.playerAmount(readLine))
    playerList
  }

  def rekCreatePlayers(
      playerList: List[Player],
      playerAmount: Int
  ): List[Player] = {
    if (playerAmount == 0) {
      createPlayers
    } else {
      printf("Bitte Namen fuer Spieler %d angeben\n", playerList.length + 1)
      val input = readLine
      val updatedList = Player(input, 0, Nil) :: playerList
      if (updatedList.length != playerAmount) {
        rekCreatePlayers(updatedList, playerAmount)
      } else {
        updatedList.reverse
      }
    }
  }

  def show(s: String): Unit = {
    println(s)
  }

  override def update = show(controller.getBoard)

  def getInputAndPrintLoop: Unit = {
    show("\n" + controller.getPlayerName + "ist an der Reihe")
    show(controller.getBoard)
    show("Welche Karte moechtest du spielen? (1|2)")

    val input = readLine
    input match
      case "1" =>
        controller.playCard(1)
      case "2" =>
        controller.playCard(2)
      case "q" | "Q" => return
      case _ =>
        show("1 oder 2 einzugeben ist doch wirklich nicht schwierig oder?")
        getInputAndPrintLoop

    controller.nextPlayer
    controller.drawCard
    getInputAndPrintLoop
  }
}
