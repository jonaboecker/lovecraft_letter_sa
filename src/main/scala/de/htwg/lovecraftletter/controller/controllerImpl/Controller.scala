package de.htwg.lovecraftletter
package controller
package controllerImpl

import model.BoardInterface
import model.BoardImpl.Board
import model.GameStateInterface
//import model.GameState
import model.PlayerInterface
//import model.Player
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
  var effectHandlerSelection:Vector[Int] = Vector(-999)

  val drawPile:Option[DrawPileInterface] = Some(new DrawPile())

  val drawPileO = drawPile match {
    case Some(b) => b
    case None => new DrawPile
  }

  override def setVarUserInput(input: Int) = userInput = input;

  override def setVarControllerState(cs: controllState, s: String) = controllerState = (cs, s);
  override def getVarControllerState = controllerState;

  override def getVarAllowedInput = allowedInput;

  override def initialize(playerList: List[PlayerInterface]): GameStateInterface = {
    state = state.updateCurrentPlayer(0)
    state = state.updateDrawPile(drawPileO.newPile)
    state = state.updatePlayer(playerList)
    for (i <- 0 until playerList.length) {
      val (newDrawPile: List[Int], hand: Int) =
        drawPileO.drawAndGet(state.drawPile)
      state = state.updateDrawPile(newDrawPile)
      val player = state.player(i).updateCardAndDiscardPile(hand, List(0))
      state = state.updatePlayer(state.player.updated(i, player))
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

  override def drawCard: GameStateInterface = {
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
    /* nextPlayer
    notifyObservers */
    state
  }

  override def playAnotherCard: GameStateInterface ={
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
    // println("play Effect")
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
    // nextPlayer
    state
  }

  override def handle: String = StateHandler.handle

  object StateHandler {
    def handle = {
      controllerState(0) match
        case controllState.standard     => getBoard
        //case controllState.selectEffect => selectEffect
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

    // def selectEffect = {
    //   allowedInput = Vector("1", "2")
    //   BoardInterface(
    //     1,
    //     Vector(state.player(state.currentPlayer).discardPile.head),
    //     0
    //   ).toString +
    //     "\nWelchen Effekt moechtest du spielen? (1|2)"
    // }
  }

  override def resetControllerState = {
    controllerState = (controllState.standard, "")
  }

  override def save(using fileIO: FileIOInterface) = {
    //val fileIO = FileIOInterface()
    fileIO.save(state)
  }
}
