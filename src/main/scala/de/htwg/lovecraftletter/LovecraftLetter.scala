package de.htwg.lovecraftletter

import aview.*
import de.htwg.lovecraftletter.LovecraftLetterModule.given_ControllerInterface
import de.htwg.lovecraftletter.controller.controllerImpl.{EffectHandler, Initializer}


@main def run(): Unit = {
  println("Welcome to LovecraftLetter")
  val gui = GUI()
  val restService = RestService()
  val effectHandler = EffectHandler()
  val initializer = Initializer()
  val tui = TUI()
}
