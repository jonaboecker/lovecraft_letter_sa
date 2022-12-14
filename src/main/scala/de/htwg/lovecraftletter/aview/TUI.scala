package de.htwg.lovecraftletter
package aview

import controller.ControllerInterface
import controller.controllState
import util.Observer

import model.Player

import scala.io.StdIn.readLine

final case class TUI(controller: ControllerInterface) extends Observer {
  controller.add(this)

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
    show(controller.handle)
    if(controller.getVarControllerState(0) == controllState.playerWins) {
        sys.exit(0)
    }
  }

//   def getInput(allowed: Vector[String]): Int = {
//     val input = readLine
//     if (!allowed.contains(input)) {
//       show("Ungueltige Eingabe, versuche es erneut.")
//       getInput(allowed)
//     } else {
//       input.toInt
//     }
//   }

  def getInputAndPrintLoop: Unit = {
    val input = readLine
    if(controller.getVarControllerState == (controllState.standard, "")) {
        input match
            case "1" =>
                controller.setVarUserInput(1)
                controller.makeTurn
            case "2" =>
                controller.setVarUserInput(2)
                controller.makeTurn
            case "q" | "Q" => return
            case "undoStep" => controller.undoStep
            case "redoStep" => controller.redoStep
            case _ =>
                show("1 oder 2 einzugeben ist doch wirklich nicht schwierig oder?")
    } else {
        if(!controller.getVarAllowedInput.contains(input)) {
            show("Ungueltige Eingabe, versuche es erneut.")
        } else {
            controller.getVarControllerState(0) match
                case controllState.selectEffect =>
                    controller.playEffect(input.toInt)
                case controllState.getEffectedPlayer =>
                    controller.playerChoosed(input.toInt)
                case controllState.getInvestigatorGuess =>
                    controller.investgatorGuessed(input.toInt)
                case controllState.getInputToPlayAnotherCard =>
                    controller.playAnotherCard2(input.toInt)
                case _ => controller.resetControllerState
        }
    }
    getInputAndPrintLoop



  }
}
