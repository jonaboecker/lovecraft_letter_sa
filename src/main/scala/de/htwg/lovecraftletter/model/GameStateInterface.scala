package de.htwg.lovecraftletter.model

import GameStateImpl._

trait GameStateInterface(
                            val currentPlayer: Int,
                            val drawPile: List[Int],
                            val player: List[PlayerInterface],
                            val currentCard: Int
                        )  {

  def updateCurrentPlayer(ncp: Int):GameStateInterface = {
    return new GameState(ncp, drawPile, player, currentCard)
  }

  def updateDrawPile(ndp: List[Int]):GameStateInterface = {
    return new GameState(currentPlayer, ndp, player, currentCard)
  }

  def updatePlayer(np: List[PlayerInterface]):GameStateInterface = {
    return new GameState(currentPlayer, drawPile, np, currentCard)
  }

  def updateCurrentCard(ncc: Int):GameStateInterface = {
    return new GameState(currentPlayer, drawPile, player, ncc)
  }

  def nextPlayer: GameStateInterface

  def drawCard: GameStateInterface

  def playCard: GameStateInterface

  def swapHandAndCurrent: GameStateInterface

  def eliminatePlayer(toEliminatePlayer: Int): GameStateInterface
}
