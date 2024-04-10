package de.htwg.lovecraftletter
package controller
package controllerImpl

import model.BoardImpl.Board
import model.GameStateInterface
import model.PlayerInterface
import model.DrawPileInterface
import model.DrawPileImpl.DrawPile
import model._
import util._
import scala.util.control.Breaks._
import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import de.htwg.lovecraftletter.LovecraftLetterModule.given

case class Initializer (val contr: ControllerInterface,
                        var state: GameStateInterface) extends InitializerInterface {

  val drawPile: Option[DrawPileInterface] = Some(DrawPile())

  private val drawPileO = drawPile match {
    case Some(b) => b
    case None => new DrawPile
  }
  override def playerAmount(input: Int): Unit = {
    contr.setVarState(state.updateCurrentPlayer(input))
    contr.setVarControllerState(controllState.initGetPlayerName, "")
    contr.notifyObservers
  }

  override def playerName(input: String): Unit = {
    state = state.addPlayer(input)
    if (state.player.length >= state.currentPlayer) {
      state = state.updatePlayer(state.player.reverse)
      initialize()
    } else {
      contr.setVarState(state)
      contr.setVarControllerState(controllState.initGetPlayerName, "")
      contr.notifyObservers
    }
  }

  override def initialize(): GameStateInterface = {
    state = state.updateCurrentPlayer(0)
    state = state.updateDrawPile(drawPileO.newPile)
    for (i <- state.player.indices) {
      val (newDrawPile: List[Int], hand: Int) =
        drawPileO.drawAndGet(state.drawPile)
      state = state.updateDrawPile(newDrawPile)
      val player = state.player(i).updateCardAndDiscardPile(hand, List(0))
      state = state.updatePlayer(state.player.updated(i, player))
    }
    contr.setVarState(state)
    contr.drawCard
    contr.resetControllerState()
    contr.notifyObservers
    state
  }

}
