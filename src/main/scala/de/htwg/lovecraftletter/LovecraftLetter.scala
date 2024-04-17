package de.htwg.lovecraftletter

import aview._
import de.htwg.lovecraftletter.LovecraftLetterModule.given_ControllerInterface


@main def run(): Unit = {
  println("Welcome to LovecraftLetter")
  val gui = GUI()
  val restService = RestService()
  val tui = TUI()
}
