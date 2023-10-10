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
import de.htwg.lovecraftletter.LovecraftLetterModule.{given}


case class Controller(
                       var state: GameStateInterface,
                       var controllerState: (controllState, String),
                       var userInput: Int
                     ) extends ControllerInterface {
  val undoManager = new UndoManager[GameStateInterface]
  var allowedInput = Vector("1", "2")
  var effectHandlerSelection: Vector[Int] = Vector(-999)

  val drawPile: Option[DrawPileInterface] = Some(new DrawPile())

  val drawPileO = drawPile match {
    case Some(b) => b
    case None => new DrawPile
  }

  override def setVarUserInput(input: Int) = userInput = input;

  override def setVarControllerState(cs: controllState, s: String) = controllerState = (cs, s);

  override def setVarState(state: GameStateInterface) = this.state = state;

  override def getVarControllerState = controllerState;

  override def getVarAllowedInput = allowedInput;

  override def runLL = {
    controllerState = (controllState.initGetPlayerAmount, "")
    notifyObservers
  }

  override def playerAmount(input: Int) = {
    state = state.updateCurrentPlayer(input)
    controllerState = (controllState.initGetPlayerName, "")
    notifyObservers
  }

  override def playerName(input: String) = {
    state = state.addPlayer(input)
    if (state.player.length >= state.currentPlayer) {
      state = state.updatePlayer(state.player.reverse)
      initialize()
    } else {
      controllerState = (controllState.initGetPlayerName, "")
      notifyObservers
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
    drawCard
    resetControllerState
    notifyObservers
    state
  }


  override def nextPlayer = {
    state = state.nextPlayer
    while (!state.player(state.currentPlayer).inGame) {
      state = state.nextPlayer
    }
    MadHandler.draw
    state
  }

  override def getPlayerName: String = {
    state.player(state.currentPlayer).name
  }

  override def drawCard: GameStateInterface = {
    state = state.drawCard
    state
  }

  override def checkForCard7or15(playedCard: Int): Int = {
    if ((state.currentCard == (7 | 15) || state.player(state.currentPlayer).hand == (7 | 15)) && !(state.currentCard == (7 | 15) && state.player(state.currentPlayer).hand == (7 | 15))) {
      if ((state.currentCard == 15 || state.player(state.currentPlayer).hand == 15) && state.player(state.currentPlayer).madCheck() > 0) {
        return playedCard
      }
      if (state.currentCard == (7 | 15)) {
        state.player(state.currentPlayer).hand match
          case 5 | 6 | 8 | 13 | 14 | 16 =>
            controllerState = (controllState.informOverPlayedEffect, "Du  musst Karte 1 spielen")
            notifyObservers
            resetControllerState
            return 1
          case _ => return playedCard
      } else if (state.player(state.currentPlayer).hand == (7 | 15)) {
        state.currentCard match
          case 5 | 6 | 8 | 13 | 14 | 16 =>
            controllerState = (controllState.informOverPlayedEffect, "Du  musst Karte 2 spielen")
            notifyObservers
            resetControllerState
            return 2
          case _ => return playedCard
      }
    }
    playedCard
  }

  override def makeTurn: GameStateInterface = {
    undoManager.doStep(state, PlayCommand(this))
    state
  }

  override def playCard: GameStateInterface = {
    val card = checkForCard7or15(userInput)
    card match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    state
  }

  override def playAnotherCard: GameStateInterface = {
    state = drawCard
    notifyObservers
    controllerState = (controllState.getInputToPlayAnotherCard, "")
    notifyObservers
    state
  }

  override def playAnotherCard2(otherCard: Int): GameStateInterface = {
    resetControllerState
    val card = checkForCard7or15(otherCard)
    card match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    state
  }

  override def undoStep: GameStateInterface = {
    state = undoManager.undoStep(state)
    notifyObservers
    state
  }

  override def redoStep: GameStateInterface = {
    state = undoManager.redoStep(state)
    notifyObservers
    state
  }

  override def playEffect(selectedEffect: Int): GameStateInterface = {
    resetControllerState
    effectHandlerSelection = Vector(selectedEffect)
    state = EffectHandler(this, state, effectHandlerSelection).initializeEffectHandler
    state
  }

  override def playerChoosed(choosedPlayer: Int): GameStateInterface = {
    resetControllerState
    effectHandlerSelection = Vector(effectHandlerSelection(0), choosedPlayer - 1)
    state = EffectHandler(this, state, effectHandlerSelection).strategy
    state
  }

  override def investgatorGuessed(guess: Int): GameStateInterface = {
    resetControllerState
    effectHandlerSelection = Vector(effectHandlerSelection(0), effectHandlerSelection(1), guess)
    state = EffectHandler(this, state, effectHandlerSelection).guessTeammateHandcard2
    state
  }

  override def checkUponWin: Boolean = {
    val players = state.player.filter(_.inGame)
    if (players.length == 1) {
      // state.player.indexOf(players(0))
      playerWins(state.player.indexOf(players(0)))
      return true
    }
    false
  }

  override def playerWins(winningPlayer: Int): GameStateInterface = {
    controllerState =
      (controllState.playerWins, state.player(winningPlayer).name)
    notifyObservers
    resetGame
    state
  }

  override def getAllowedPlayerForPlayerSelection: Vector[String] = {
    val res =
    rekGetAllowedPlayerForPlayerSelection(1, state.player, Vector[String]())
    res
  }

  override def rekGetAllowedPlayerForPlayerSelection(
                                                      counter: Int,
                                                      playerList: List[PlayerInterface],
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

    def drawMad: GameStateInterface = {
      val tempCurrentPlayer = state.currentPlayer
      for (x <- 0 until state.player(tempCurrentPlayer).madCheck()) {
        if (state.player(tempCurrentPlayer).inGame) {
          drawCard
          if (state.currentCard > 8) {
            eliminatePlayer(tempCurrentPlayer)
            nextPlayer
          } else {
            state = state.playCard
            //drawCard
          }
        }
      }
      drawCard

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
        state = state.playCard
        playEffect(2)
      } else {
        state = state.playCard
        playEffect(1)
      }
    }
  }

  override def eliminatePlayer(player: Int): GameStateInterface = {
    state = state.eliminatePlayer(player)
    controllerState =
      (controllState.tellEliminatedPlayer, state.player(player).name)
    notifyObservers
    checkUponWin
    state
  }

  override def handle: String = StateHandler.handle

  object StateHandler {
    def handle = {
      controllerState(0) match
        case controllState.standard => getBoard
        case controllState.initGetPlayerAmount =>
          allowedInput = Vector("3", "4", "5", "6")
          "Bitte Spieleranzahl zwischen 3 und 6 angeben"
        case controllState.initGetPlayerName =>
          allowedInput = Vector("")
          "Bitte Name für Spieler " + (state.player.length + 1) + " angeben"
        case controllState.tellEliminatedPlayer =>
          "Spieler " + controllerState(1) + " wurde eliminiert"
        case controllState.playerWins =>
          allowedInput = Vector("j", "n")
          "Spieler " + controllerState(1) + " hat die Runde gewonnen\nEs wird eine neue Runde gestartet."
        case controllState.getEffectedPlayer =>
          allowedInput = getAllowedPlayerForPlayerSelection
          "Waehle einen Spieler auf den du deine Aktion anwenden willst " + allowedInput.toString()
        case controllState.getInvestigatorGuess =>
          allowedInput = Vector("0", "2", "3", "4", "5", "6", "7", "8")
          "Welchen Wert der Handkarte raetst du (0|2-8)"
        case controllState.informOverPlayedEffect => controllerState(1)
        case controllState.getInputToPlayAnotherCard =>
          allowedInput = Vector("1", "2")
          "Welche Karte moechtest du spielen? (1|2)"
    }

    def getBoard = {
      val currentPlayer = state.player(state.currentPlayer)
      val board = Board(3, Vector(
        state.currentCard,
        currentPlayer.hand,
        currentPlayer.discardPile.head
      ), 1)

      "\n" + getPlayerName + " ist an der Reihe" + isCurrentPlayerMad + "\n" +
        board.toString + "\nWelche Karte moechtest du spielen? (1|2)"
    }

    def isCurrentPlayerMad: String = {
      if (state.player(state.currentPlayer).madCheck() > 0) {
        return " und wahnsinnig"
      }
      ""
    }
  }

  override def resetControllerState = {
    controllerState = (controllState.standard, "")
  }

  override def save(using fileIO: FileIOInterface) = {
    fileIO.save(state)
  }

  override def load(using fileIO: FileIOInterface) = {
    state = fileIO.load(state)
    notifyObservers
  }

  override def resetGame = {
    initialize()
  }
}
