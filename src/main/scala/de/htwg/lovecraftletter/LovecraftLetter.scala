package de.htwg.lovecraftletter

import aview.TUI
import model.GameState

//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def run: Unit = {
  println("Welcome to LovecraftLetter")
  val gamestate = new GameState(0,Nil,Nil)
  val controller = new Controller(gamestate)
  val tui = TUI(controller)
  tui.run
  // val tempVec = Vector(1, 1, 1)
  // print(new Board(tempVec))
  // getInputAndPrintLoop()
}

