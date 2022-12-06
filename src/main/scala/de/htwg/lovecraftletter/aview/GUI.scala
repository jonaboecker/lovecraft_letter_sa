package de.htwg.lovecraftletter
package aview

import controller._
import util.Observer
import model._

import scala.swing._
//import scala.swing.event._

class GUI(controller: Controller) extends Frame with Observer {
    controller.add(this)

    val boardTA = TextArea(23, 82)
    boardTA.font =(new Font("Monospaced", 0,14))
    boardTA.editable = false

    val effectTA = TextArea(2, 82)
    effectTA.font =(new Font("Monospaced", 0,16))
    effectTA.editable = false

    var inputCO = ListView(Vector("1", "2"))

    title = "Lovecraft Letter"
    menuBar = new MenuBar{
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
        }
    }
    contents = new BorderPanel {
        //add(new Label("Willkommen bei Lovecraft Letter"), BorderPanel.Position.North)
        add(boardTA, BorderPanel.Position.North)
        add(effectTA, BorderPanel.Position.Center)
        add(inputCO, BorderPanel.Position.South)
    }

    pack()
    centerOnScreen()
    open()


    override def update = {
        show(controller.StateHandler.handle)
        controller.controllerState(0) match
            case controllState.standard =>
            case controllState.selectEffect =>
                inputCO.listData = Vector("1", "2")
                //controller.playEffect(getInput(Vector("1", "2")))
            case controllState.getEffectedPlayer =>
                inputCO.listData = controller.getAllowedPlayerForPlayerSelection
                //controller.userInput = getInput(
                //    controller.getAllowedPlayerForPlayerSelection
                //)
            case controllState.getInvestigatorGuess =>
                inputCO.listData = Vector("0", "2", "3", "4", "5", "6", "7", "8")
                //controller.userInput = getInput(
                //  Vector("0", "2", "3", "4", "5", "6", "7", "8")
                //)
            case controllState.getInputToPlayAnotherCard =>
                inputCO.listData = Vector("1", "2")
                //todo: Passt das so in der TUI?
                //getInput(Vector("1", "2"))
            case _ => controller.controllerState = (controllState.standard, "")
    }

    def show(output:String) = {
        if(controller.controllerState == (controllState.standard,"")) {
            boardTA.text = output
            effectTA.text = "Welche Karte moechtest du spielen? (1|2)" + "correct"
            inputCO.listData = Vector("1", "2")
        } else {
            effectTA.text = output
        } 
    }

}
