package de.htwg.lovecraftletter
package aview

import controller._
import util.Observer
import model._

import scala.io.StdIn.readLine

final case class TUI(controller: Controller) extends Observer {
  controller.add(this)
  controller.effectHandler = EffectHandler(controller, null, -1, -1)
  controller.effectHandler.add(this)

  def runLL = {
    val playerList: List[Player] = createPlayers
    show("Viel Spass beim spielen")
    controller.initialize(playerList)
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
      val updatedList = Player(input, 0, Nil, true) :: playerList
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

  override def update = {
    show(controller.StateHandler.handle)
    controller.controllerState(0) match
      case controllState.standard =>
      case controllState.selectEffect =>
        controller.playEffect(getInput(Vector("1", "2")))
      case controllState.getEffectedPlayer => controller.effectHandler.setUserInput(getInput(controller.getAllowedPlayerForPlayerSelection))
      case controllState.getInvestigatorGuess => controller.effectHandler.setUserInput(getInput(Vector("2", "3", "4", "5", "6", "7", "8")))
      case _ => controller.controllerState = (controllState.standard, "")
  }

  def getInput(allowed: Vector[String]): Int = {
    val input = readLine
    if (!allowed.contains(input)) {
      show("Ungueltige Eingabe, versuche es erneut.")
      getInput(allowed)
    } else {
      input.toInt
    }
  }

  def getInputAndPrintLoop: Unit = {
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
  }
}
