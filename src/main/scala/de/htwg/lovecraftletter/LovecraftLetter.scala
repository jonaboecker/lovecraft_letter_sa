package de.htwg.lovecraftletter

import aview._
import model.GameStateInterface
import model.GameStateImpl._
import controller.ControllerInterface
import controller.controllState
import controller.controllerImpl._

//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def run: Unit = {
  println("Welcome to LovecraftLetter")
  val gamestate = new GameState(0, Nil, Nil, 0)
  val controller: ControllerInterface =
    new Controller(gamestate, (controllState.standard, ""), -999)
  val gui = GUI(controller)
  val tui = TUI(controller)
  tui.runLL
  // val tempVec = Vector(1, 1, 1)
  // print(new Board(tempVec))
  // getInputAndPrintLoop()
}
