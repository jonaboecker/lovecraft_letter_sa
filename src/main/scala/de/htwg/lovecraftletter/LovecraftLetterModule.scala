package de.htwg.lovecraftletter

import de.htwg.lovecraftletter.controller.ControllerInterface
import de.htwg.lovecraftletter.controller.controllState
import de.htwg.lovecraftletter.model.GameStateInterface
import de.htwg.lovecraftletter.model.BoardInterface
import de.htwg.lovecraftletter.model.DrawPileInterface
import de.htwg.lovecraftletter.model.PlayerInterface

import de.htwg.lovecraftletter.controller.controllerImpl.Controller
import de.htwg.lovecraftletter.model.GameStateImpl.GameState
import de.htwg.lovecraftletter.model.DrawPileImpl.DrawPile
import de.htwg.lovecraftletter.model.BoardImpl.Board
import de.htwg.lovecraftletter.model.PlayerImpl.Player
import de.htwg.lovecraftletter.model._
import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import de.htwg.lovecraftletter.model.FileIO.FileIOImpl._

object LovecraftLetterModule {
  given ControllerInterface = Controller(GameState(0, Nil, Nil, 0), (controllState.standard, ""), -999)
  given FileIOInterface = FileIOJSON()
  //given DrawPileInterface = DrawPile()
}
