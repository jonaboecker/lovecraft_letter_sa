package de.htwg.lovecraftletter

import aview._
import model.GameStateInterface
import model.GameStateImpl._
import controller.ControllerInterface
import controller.controllState
import controller.controllerImpl._
import de.htwg.lovecraftletter.LovecraftLetterModule.given_ControllerInterface



//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def run: Unit = {
  println("Welcome to LovecraftLetter")
  val gui = GUI()
  val tui = TUI()
}
