package de.htwg.lovecraftletter
package aview

import controller.ControllerInterface
import controller.controllState
import util.Observer

import model.PlayerInterface
import model.PlayerImpl.Player

import LovecraftLetterModule.given

import scala.io.StdIn.readLine

final class TUI(using controller: ControllerInterface) extends Observer {
  controller.add(this)
  controller.runLL()
  getInputAndPrintLoop


  def show(s: String): Unit = {
    println(s)
  }

  override def update = {
    show(controller.handle)
  }

  def getInputAndPrintLoop: Unit = {
    val input = readLine
    if (controller.getVarControllerState == (controllState.standard, "")) {
      input match
        case "1" =>
          controller.setVarUserInput(1)
          controller.makeTurn
        case "2" =>
          controller.setVarUserInput(2)
          controller.makeTurn
        case "q" | "Q" => sys.exit(0)
        case "undoStep" => controller.undoStep
        case "redoStep" => controller.redoStep
        case "save" =>
          controller.save
          show("speichern erfolgreich")
        case "load" => controller.load
        case _ =>
          show("1 oder 2 einzugeben ist doch wirklich nicht schwierig oder?")
    } else {
      if (controller.getVarAllowedInput != Vector("") && !controller.getVarAllowedInput.contains(input)) {
        show("Ungueltige Eingabe, versuche es erneut.")
      } else {
        controller.getVarControllerState(0) match
          case controllState.getEffectedPlayer =>
            controller.playerChoosed(input.toInt)
          case controllState.getInvestigatorGuess =>
            controller.investgatorGuessed(input.toInt)
          case controllState.getInputToPlayAnotherCard =>
            controller.playAnotherCard2(input.toInt)
          case controllState.initGetPlayerAmount =>
            controller.playerAmount(input.toInt)
          case controllState.initGetPlayerName =>
            controller.playerName(input)
          case _ => controller.resetControllerState()
      }
    }
    getInputAndPrintLoop
  }
}
