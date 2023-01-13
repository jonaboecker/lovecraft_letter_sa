package de.htwg.lovecraftletter
package model.GameStateImpl

import de.htwg.lovecraftletter.model._
import model.DrawPileImpl.DrawPile

final case class GameState(override val currentPlayer: Int, override val drawPile: List[Int], override val player: List[PlayerInterface], override val currentCard: Int)
     extends GameStateInterface (currentPlayer, drawPile, player, currentCard){

  override def nextPlayer: GameState = {
    GameState(
      (currentPlayer + 1) % player.length,
      drawPile,
      player,
      currentCard
    )
  }

  override def drawCard: GameState = {
    val drawPileObject = new DrawPile
    val (tempDrawPile: List[Int], tempCard: Int) =
      drawPileObject.drawAndGet(drawPile)
    GameState(currentPlayer, tempDrawPile, player, tempCard)
  }

  override def playCard: GameState = {
    val tempPlayer = player.updated(
      currentPlayer,
      player(currentPlayer).discardCard(currentCard)
    )
    GameState(currentPlayer, drawPile, tempPlayer, 0)
  }

  override def swapHandAndCurrent: GameState = {
    val tempPlayer = player.updated(
      currentPlayer,
      player(currentPlayer).changeHand(currentCard)
    )
    GameState(currentPlayer, drawPile, tempPlayer, player(currentPlayer).hand)
  }

  override def eliminatePlayer(toEliminatePlayer: Int): GameState = {
    val tempPlayer =
      player.updated(
        toEliminatePlayer,
        player(toEliminatePlayer).eliminatePlayer()
      )
    GameState(currentPlayer, drawPile, tempPlayer, currentCard)
  }
}
