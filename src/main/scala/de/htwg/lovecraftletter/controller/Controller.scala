package de.htwg.lovecraftletter
package controller

import model._
import util.Observable
import scala.util.control.Breaks._

case class Controller(var state: GameState, var controllerState: String) extends Observable {
  val drawPileO = new DrawPile

  def initialize(playerList: List[Player]): GameState = {
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
    notifyObservers
    state
  }

  def playerAmount(input: String): Int = {
    input match
      case "3" => 3
      case "4" => 4
      case "5" => 5
      case "6" => 6
      case _   => 0
  }

  def nextPlayer = {
    state = state.nextPlayer
    while (!state.player(state.currentPlayer).inGame) {
      state = state.nextPlayer
    }
    state
  }

  def getPlayerName: String = {
    state.player(state.currentPlayer).name
  }

  def drawCard: GameState = {
    state = state.drawCard
    state
  }

  def playCard(playedCard: Int): GameState = {
    playedCard match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    nextPlayer
    MadHandler.draw
    notifyObservers
    state
  }

  def playEffect(selecterEffect: String) = {
    controllerState = "standard"
    println("play Effect")
  }

  def checkUponWin: Boolean = {
    if (state.player.filter(_.inGame).length == 1) {
      // change state to player x won
      // NO
      return true
    }
    false
  }

  object MadHandler {
    def draw =
      if (state.player(state.currentPlayer).madCheck() > 0) drawMad
      else drawNormal

    def drawNormal = {
      drawCard
    }

    def drawMad = {
      for (
        x <- 0 until state.player(state.currentPlayer).madCheck()
        if state.player(state.currentPlayer).inGame
      ) {
        drawCard
        if (state.currentCard > 8) {
          state = state.eliminatePlayer
          checkUponWin
          // todo: state change to kick player
          // break
        }
        state = state.playCard
      }
      // todo: state change to surived madcheck
      drawCard
    }

    def play =
      if (state.player(state.currentPlayer).madCheck() > 0) playMad
      else playNormal

    def playNormal = {
      state = state.playCard
    }

    def playMad = {
      if (state.currentCard > 8) {
        controllerState = "selectEffect"
        state = state.playCard
        notifyObservers
      }
      state = state.playCard
    }
  }

  object StateHandler {
    def handle = {
        controllerState match
            case "standard" => getBoard
            case "selectEffect" => selectEffect
            case _ => "du Idiot"
    }

    def getBoard = {
        val currentPlayer = state.player(state.currentPlayer)
        val board = Board(3,
        Vector(
            state.currentCard,
            currentPlayer.hand,
            currentPlayer.discardPile.head
            ),
            1)
            "\n" + getPlayerName + " ist an der Reihe\n" + 
            board.toString + "\nWelche Karte moechtest du spielen? (1|2)"
    }

    def selectEffect = {
        Board(1, Vector(state.player(state.currentPlayer).discardPile.head),0).toString + 
        "\nWelchen Effekt moechtest du spielen? (1|2)"
    }

  }
}
