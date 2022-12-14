package de.htwg.lovecraftletter.model

trait GameStateInterface {

  def nextPlayer: GameState

  def drawCard: GameState

  def playCard: GameState

  def swapHandAndCurrent: GameState

  def eliminatePlayer(toEliminatePlayer: Int): GameState
}
