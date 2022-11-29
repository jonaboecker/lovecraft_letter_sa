package de.htwg.lovecraftletter

import aview.TUI
import model.GameState
import controller._

//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def run: Unit = {
  println("Welcome to LovecraftLetter")
  val gamestate = new GameState(0, Nil, Nil, 0)
  val controller: Controller =
    new Controller(gamestate, (controllState.standard, ""), -999)
  val tui = TUI(controller)
  tui.runLL
  // val tempVec = Vector(1, 1, 1)
  // print(new Board(tempVec))
  // getInputAndPrintLoop()
}
