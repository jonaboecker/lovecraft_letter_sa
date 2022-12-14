package de.htwg.lovecraftletter
package controller

import model.BoardInterface
import model.Board
import model.GameStateInterface
import model.GameState
import model.PlayerInterface
import model.Player
import model.DrawPileInterface
import model.DrawPile
import util._
import scala.util.control.Breaks._



case class Controller(
                       var state: GameState,
                       var controllerState: (controllState, String),
                       var userInput: Int
                     ) extends ControllerInterface {
  val undoManager = new UndoManager[GameState]
  var allowedInput = Vector("1", "2")
  var effectHandlerSelection:Vector[Int] = Vector(-999)

  val drawPile:Option[DrawPile] = Some(DrawPile())

  val drawPileO = drawPile match {
    case Some(b) => b
    case None => new DrawPile
  }

  override def setVarUserInput(input: Int) = userInput = input;
  override def getVarControllerState = controllerState;

  override def getVarAllowedInput = allowedInput;

  override def initialize(playerList: List[Player]): GameState = {
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

  override def playerAmount(input: String): Int = {
    input match
      case "3" => 3
      case "4" => 4
      case "5" => 5
      case "6" => 6
      case _   => 0
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

  override def drawCard: GameState = {
    state = state.drawCard
    state
  }

  override def checkForCard7or15(playedCard: Int): Int = {
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

  override def makeTurn: GameState = {
    undoManager.doStep(state, PlayCommand(this))
    state
  }

  override def playCard: GameState = {
    val card = checkForCard7or15(userInput)
    card match
      case 1 => MadHandler.play // todo change state to
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    /* nextPlayer
    notifyObservers */
    state
  }

  override def playAnotherCard: GameState ={
    state = drawCard
    notifyObservers
    controllerState = (controllState.getInputToPlayAnotherCard, "")
    notifyObservers
    state
  }

  override def playAnotherCard2(otherCard: Int): GameState = {
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

  override def undoStep: GameState = {
    state = undoManager.undoStep(state)
    notifyObservers
    state
  }

  override def redoStep: GameState = {
    state = undoManager.redoStep(state)
    notifyObservers
    state
  }

  override def playEffect(selectedEffect: Int): GameState = {
    resetControllerState
    effectHandlerSelection = Vector(selectedEffect)
    state = EffectHandler(this, state, effectHandlerSelection).initializeEffectHandler
    state
    // println("play Effect")
  }

  override def playerChoosed(choosedPlayer: Int): GameState = {
    resetControllerState
    effectHandlerSelection = Vector(effectHandlerSelection(0), choosedPlayer - 1)
    state = EffectHandler(this, state, effectHandlerSelection).strategy
    state
  }

  override def investgatorGuessed(guess: Int): GameState = {
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

  override def playerWins(winningPlayer: Int): GameState = {
    controllerState =
      (controllState.playerWins, state.player(winningPlayer).name)
    // checking if player won the game
    // if not start new round
    notifyObservers
    // for now stop here
    // System.exit(0)
    state
  }

  override def getAllowedPlayerForPlayerSelection: Vector[String] = {
    // val res = for (x <- 0 until (state.player.length)) yield (x + 1).toString
    // todo current player should not in Vector
    // todo eliminated Player schould not in Vector
    val res =
    rekGetAllowedPlayerForPlayerSelection(1, state.player, Vector[String]())
    //print(res)
    // val res2 = res.toVector.drop(state.currentPlayer)
    // print(res2)
    res
  }

  override def rekGetAllowedPlayerForPlayerSelection(
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

  override def eliminatePlayer(player: Int): GameState = {
    state = state.eliminatePlayer(player)
    controllerState =
      (controllState.tellEliminatedPlayer, state.player(player).name)
    notifyObservers
    checkUponWin
    // nextPlayer
    state
  }

  override def handle: String = StateHandler.handle

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
      val board = Board(
        3,
        Vector(
          state.currentCard,
          currentPlayer.hand,
          currentPlayer.discardPile.head
        ),
        1
      )

      "\n" + getPlayerName + " ist an der Reihe" + isCurrentPlayerMad + "\n" +
        board.toString + "\nWelche Karte möchtest du spielen? (1|2)"
    }

    def isCurrentPlayerMad: String = {
      if (state.player(state.currentPlayer).madCheck() > 0) {
        return " und wahnsinnig"
      }
      ""
    }

    def selectEffect = {
      allowedInput = Vector("1", "2")
      Board(
        1,
        Vector(state.player(state.currentPlayer).discardPile.head),
        0
      ).toString +
        "\nWelchen Effekt moechtest du spielen? (1|2)"
    }
  }

  override def resetControllerState = {
    controllerState = (controllState.standard, "")
  }
}
