package de.htwg.lovecraftletter
package aview

import controller.ControllerInterface
import controller.controllState
import util.Observer

import scala.swing._
import scala.swing.event._
import scala.swing.ListView._
import de.htwg.lovecraftletter.controller.controllState

import LovecraftLetterModule.{given}

class GUI(using controller: ControllerInterface) extends Frame with Observer {
  controller.add(this)

  val hintTA = TextArea(5, 50)
  hintTA.font = (new Font("Monospaced", 0, 16))
  hintTA.editable = false
  hintTA.text = "Willkommen bei Lovecraft Letter"

  val hintScroll = new ScrollPane(hintTA) {
    horizontalScrollBarPolicy = ScrollPane.BarPolicy.Never
    verticalScrollBarPolicy = ScrollPane.BarPolicy.AsNeeded // Oder Always, je nach Bedarf
  }

  val boardTA = TextArea(21, 82)
  boardTA.font = (new Font("Monospaced", 0, 14))
  boardTA.editable = false

  val effectTA = TextArea(2, 82)
  effectTA.font = (new Font("Monospaced", 0, 16))
  effectTA.editable = false

  var inputCO = ListView(Vector("1", "2", "1", "2", "1", "2", "1", "2"))

  title = "Lovecraft Letter"
  menuBar = new MenuBar {
    contents += new Menu("Optionen") {
      contents += new MenuItem(Action("Tschuess") {
        sys.exit(0)
      })
      contents += new MenuItem(Action("Rueckgaengig") {
        controller.undoStep
      })
      contents += new MenuItem(Action("Rueckgaengig Rueckgaengig machen") {
        controller.redoStep
      })
      contents += new MenuItem(Action("Speichern") {
        controller.save
      })
      contents += new MenuItem(Action("Laden") {
        controller.load
      })
      contents += MenuItem(Action("mach mal") {
        handle
      })
    }
  }

  contents = new BorderPanel {
    //add(new Label("Willkommen bei Lovecraft Letter"), BorderPanel.Position.North)
    add(hintScroll, BorderPanel.Position.East)
    add(boardTA, BorderPanel.Position.North)
    add(effectTA, BorderPanel.Position.Center)
    add(inputCO, BorderPanel.Position.South)
  }

  pack()
  inputCO.listData = Vector()
  centerOnScreen()
  open()


  override def update = {
    show(controller.handle)
    controller.getVarControllerState(0) match
      case controllState.standard =>
      case controllState.initGetPlayerAmount =>
        inputCO.listData = Vector("3", "4", "5", "6")
      case controllState.initGetPlayerName =>
        inputCO.listData = Vector("Player1", "Player2", "Player3", "Player4", "Player5", "Player6", "Wenn du was anderes willst Spiel in der TUI du Trottel!")
      case controllState.getEffectedPlayer =>
        inputCO.listData = controller.getAllowedPlayerForPlayerSelection
      case controllState.getInvestigatorGuess =>
        inputCO.listData = Vector("0", "2", "3", "4", "5", "6", "7", "8")
      case controllState.getInputToPlayAnotherCard =>
        inputCO.listData = Vector("1", "2")
      case controllState.playerWins =>
        inputCO.listData = Vector("j", "n")
      case _ =>
  }

  def handle = {
    controller.getVarControllerState(0) match
      case controllState.standard =>
        inputCO.selection.items(0) match
          case "1" =>
            controller.setVarUserInput(1)
            controller.makeTurn
          case "2" =>
            controller.setVarUserInput(2)
            controller.makeTurn
      case controllState.getEffectedPlayer =>
        controller.playerChosen(inputCO.selection.items(0).toInt)
      case controllState.getInvestigatorGuess =>
        controller.investgatorGuessed(inputCO.selection.items(0).toInt)
      case controllState.getInputToPlayAnotherCard =>
        controller.playAnotherCard2(inputCO.selection.items(0).toInt)
      case controllState.initGetPlayerAmount =>
        controller.playerAmount(inputCO.selection.items(0).toInt)
      case controllState.initGetPlayerName =>
        controller.playerName(inputCO.selection.items(0))
      case _ => controller.resetControllerState()
  }

  def show(output: String) = {
    if (controller.getVarControllerState == (controllState.standard, "")) {
      boardTA.text = output.dropRight(41)
      effectTA.text = "Welche Karte moechtest du spielen? (1|2)"
      inputCO.listData = Vector("1", "2")
    } else if (controller.getVarControllerState(0) == controllState.informOverPlayedEffect || controller.getVarControllerState(0) == controllState.playerWins || controller.getVarControllerState(0) == controllState.tellEliminatedPlayer) {
      //Dialog.showMessage(this, controller.handle)
      hintTA.text = hintTA.text + "\n\n" + controller.handle
      hintScroll.verticalScrollBar.value = hintScroll.verticalScrollBar.maximum
    } else {
      effectTA.text = output
    }
  }

}
