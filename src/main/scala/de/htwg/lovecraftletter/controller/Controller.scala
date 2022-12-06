package de.htwg.lovecraftletter
package controller

import model._
import util._
import scala.util.control.Breaks._

enum controllState {
  case standard
  case selectEffect
  case tellEliminatedPlayer
  case playerWins
  case getEffectedPlayer
  case getInvestigatorGuess
  case informOverPlayedEffect
  case getInputToPlayAnotherCard
}

case class Controller(
    var state: GameState,
    var controllerState: (controllState, String),
    var userInput: Int
) extends Observable {
  val undoManager = new UndoManager[GameState]

  val drawPile:Option[DrawPile] = Some(DrawPile())

  val drawPileO = drawPile match {
  case Some(b) => b
  case None => new DrawPile
 }


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
    MadHandler.draw
    state
  }

  def getPlayerName: String = {
    state.player(state.currentPlayer).name
  }

  def drawCard: GameState = {
    state = state.drawCard
    state
  }

  def checkForCard7or15(playedCard: Int): Int = {
    if((state.currentCard == (7|15) || state.player(state.currentPlayer).hand == (7|15)) && !(state.currentCard == (7|15) && state.player(state.currentPlayer).hand == (7|15)) ) {
        if((state.currentCard == 15 || state.player(state.currentPlayer).hand == 15) && state.player(state.currentPlayer).madCheck() > 0) {
            return playedCard
        }
        if (state.currentCard == (7|15)){
            state.player(state.currentPlayer).hand match
                case 5|6|8|13|14|16 =>
                    controllerState = (controllState.informOverPlayedEffect, "Du  musst Karte 1 spielen")
                    notifyObservers
                    resetControllerState
                    return 1
                case _ => return playedCard
        } else if (state.player(state.currentPlayer).hand == (7|15)){
             state.currentCard match
                case 5|6|8|13|14|16 =>
                    controllerState = (controllState.informOverPlayedEffect, "Du  musst Karte 2 spielen")
                    notifyObservers
                    resetControllerState
                    return 2
                case _ => return playedCard
        }
    }
    playedCard
  }

  def makeTurn: GameState = {
    undoManager.doStep(state, PlayCommand(this))
    state
  }

  def playCard: GameState = {
    val card = checkForCard7or15(userInput)
    card match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    nextPlayer
    notifyObservers
    state
  }

  def playAnotherCard: GameState ={
    state = drawCard
    notifyObservers
    controllerState = (controllState.getInputToPlayAnotherCard, "")
    notifyObservers
    resetControllerState
    val card = checkForCard7or15(userInput)
    card match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    state
  }

  def undoStep: GameState = {
    state = undoManager.undoStep(state)
    notifyObservers
    state
  }

  def redoStep: GameState = {
    state = undoManager.redoStep(state)
    notifyObservers
    state
  }

  def playEffect(selectedEffect: Int): GameState = {
    resetControllerState
    state = EffectHandler(this, state, selectedEffect).strategy
    state
    // println("play Effect")
  }

  def checkUponWin: Boolean = {
    val players = state.player.filter(_.inGame)
    if (players.length == 1) {
      // state.player.indexOf(players(0))
      playerWins(state.player.indexOf(players(0)))
      return true
    }
    false
  }

  def playerWins(winningPlayer: Int): GameState = {
    controllerState =
      (controllState.playerWins, state.player(winningPlayer).name)
    // checking if player won the game
    // if not start new round
    notifyObservers
    // for now stop here
    // System.exit(0)
    state
  }

  def getAllowedPlayerForPlayerSelection: Vector[String] = {
    // val res = for (x <- 0 until (state.player.length)) yield (x + 1).toString
    // todo current player should not in Vector
    // todo eliminated Player schould not in Vector
    val res =
      rekGetAllowedPlayerForPlayerSelection(1, state.player, Vector[String]())
    print(res)
    // val res2 = res.toVector.drop(state.currentPlayer)
    // print(res2)
    res
  }

  def rekGetAllowedPlayerForPlayerSelection(
      counter: Int,
      playerList: List[Player],
      allowedPlayers: Vector[String]
  ): Vector[String] = {
    if (playerList.length == 0) {
      return allowedPlayers
    } else {
      if (
        playerList.head.inGame && playerList.head != state.player(
          state.currentPlayer
        ) && state.player(counter - 1).discardPile.head != 4 && state
          .player(counter - 1)
          .discardPile
          .head != 12
      ) {
        var tempAllowedPlayers: Vector[String] =
          allowedPlayers.appended(counter.toString())
        return rekGetAllowedPlayerForPlayerSelection(
          counter + 1,
          playerList.tail,
          tempAllowedPlayers
        )
      } else {
        var tempAllowedPlayers: Vector[String] = allowedPlayers
        return rekGetAllowedPlayerForPlayerSelection(
          counter + 1,
          playerList.tail,
          tempAllowedPlayers
        )
      }
    }
  }

  object MadHandler {
    def draw =
      if (
        state.player(state.currentPlayer).discardPile.head != 12 && state
          .player(state.currentPlayer)
          .madCheck() > 0
      ) drawMad
      else drawNormal

    def drawNormal = {
      drawCard
    }

    def drawMad: GameState = {
      val tempCurrentPlayer = state.currentPlayer
      for (x <- 0 until state.player(tempCurrentPlayer).madCheck()) {
        if (state.player(tempCurrentPlayer).inGame) {
          drawCard
          if (state.currentCard > 8) {
            eliminatePlayer(tempCurrentPlayer)
            nextPlayer
          } else {
            state = state.playCard
            drawCard
          }
        }
      }

      state
    }

    def play =
      if (state.player(state.currentPlayer).madCheck() > 0) playMad
      else playNormal

    def playNormal = {
      state = state.playCard
      playEffect(1)
    }

    def playMad = {
      if (state.currentCard > 8) {
        controllerState = (controllState.selectEffect, "")
        state = state.playCard
        notifyObservers
      } else {
        state = state.playCard
        playEffect(1)
      }
    }
  }

  def eliminatePlayer(player: Int): GameState = {
    state = state.eliminatePlayer(player)
    controllerState =
      (controllState.tellEliminatedPlayer, state.player(player).name)
    notifyObservers
    checkUponWin
    // nextPlayer
    state
  }

  object StateHandler {
    def handle = {
      controllerState(0) match
        case controllState.standard     => getBoard
        case controllState.selectEffect => selectEffect
        case controllState.tellEliminatedPlayer =>
          "Spieler " + controllerState(1) + " wurde eliminiert"
        case controllState.playerWins =>
          "Spieler " + controllerState(1) + " hat die Runde gewonnen"
        case controllState.getEffectedPlayer =>
          "Waele einen Spieler auf den du deine Aktion anwenden willst"
        case controllState.getInvestigatorGuess =>
          "Welchen Wert der Handkarte raetst du (0|2-8)"
        case controllState.informOverPlayedEffect => controllerState(1)
        case controllState.getInputToPlayAnotherCard =>
          "Welche Karte moechtest du spielen? (1|2)"
    }

    def getBoard = {
      val currentPlayer = state.player(state.currentPlayer)
      val board = Board(
        3,
        Vector(
          state.currentCard,
          currentPlayer.hand,
          currentPlayer.discardPile.head
        ),
        1
      )
      "\n" + getPlayerName + " ist an der Reihe\n" +
        board.toString + "\nWelche Karte moechtest du spielen? (1|2)"
    }

    def selectEffect = {
      Board(
        1,
        Vector(state.player(state.currentPlayer).discardPile.head),
        0
      ).toString +
        "\nWelchen Effekt moechtest du spielen? (1|2)"
    }
  }

  def resetControllerState = {
    controllerState = (controllState.standard, "")
  }
}
