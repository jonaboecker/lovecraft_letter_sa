package de.htwg.lovecraftletter.controller

import de.htwg.lovecraftletter.model.{GameState, Player}
import de.htwg.lovecraftletter.util.Observable

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

trait ControllerInterface extends Observable{

  def setVarUserInput(input: Int): Unit

  def getVarControllerState: (controllState, String)

  def getVarAllowedInput: Vector[String]

  def initialize(playerList: List[Player]): GameState

  def playerAmount(input: String): Int

  def nextPlayer: GameState

  def getPlayerName: String

  def drawCard: GameState

  def checkForCard7or15(playedCard: Int): Int

  def makeTurn: GameState

  def playCard: GameState

  def playAnotherCard: GameState

  def playAnotherCard2(otherCard: Int): GameState

  def undoStep: GameState

  def redoStep: GameState

  def playEffect(selectedEffect: Int): GameState

  def playerChoosed(choosedPlayer: Int): GameState

  def investgatorGuessed(guess: Int): GameState

  def checkUponWin: Boolean

  def playerWins(winningPlayer: Int): GameState

  def getAllowedPlayerForPlayerSelection: Vector[String]

  def rekGetAllowedPlayerForPlayerSelection(
                                             counter: Int,
                                             playerList: List[Player],
                                             allowedPlayers: Vector[String]
                                           ): Vector[String]

  def eliminatePlayer(player: Int): GameState

  def handle: String

  def resetControllerState: Unit
}
