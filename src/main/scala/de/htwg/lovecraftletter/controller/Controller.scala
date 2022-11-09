package de.htwg.lovecraftletter
package controller

import model._
import util.Observable

import scala.io.StdIn.readLine

case class Controller(var state: GameState) extends Observable {
  val drawPileO = new DrawPile

  def initialize(playerList: List[Player]) = {
    state.currentPlayer = 0;
    state.drawPile = drawPileO.newPile
    state.player = playerList
    for (i <- 0 until playerList.length) {
      val (newDrawPile: List[Int], hand: Int) =
        drawPileO.drawAndGet(state.drawPile)
      state.drawPile = newDrawPile
      val player = state.player(i).updateCardAndDiscardPile(hand, List(0))
      state.player = state.player.updated(i, player)
    }
    drawCard
  }

  def playerAmount(input: String): Int = {
    input match
      case "3" => 3
      case "4" => 4
      case "5" => 5
      case "6" => 6
      case _   => 0
  }

  def getBoard = {
    val currentPlayer = state.player(state.currentPlayer)
    val board = Board(
      Vector(
        state.currentCard,
        currentPlayer.hand,
        currentPlayer.discardPile.head
      ),
      1
    )
    board.toString
  }

  def nextPlayer = {
    state = state.nextPlayer
    state
  }

  def getPlayerName: String = {
    state.player(state.currentPlayer).name
  }

  def drawCard: GameState = {
    state = state.drawCard
    state
  }

  def playCard(playedCard: Int) = {
    state = playedCard match
      case 1 => state.playCard
      case 2 => state.swapHandAndCurrent.playCard
    state
  }
}
