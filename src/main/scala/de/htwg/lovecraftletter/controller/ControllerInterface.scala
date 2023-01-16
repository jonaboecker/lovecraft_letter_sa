package de.htwg.lovecraftletter.controller

import de.htwg.lovecraftletter.model.{GameStateInterface, PlayerInterface}
import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import de.htwg.lovecraftletter.util.Observable
import de.htwg.lovecraftletter.model.GameStateInterface

enum controllState {
  case standard
  case tellEliminatedPlayer
  case playerWins
  case getEffectedPlayer
  case getInvestigatorGuess
  case informOverPlayedEffect
  case getInputToPlayAnotherCard
}

trait ControllerInterface extends Observable{

  def setVarUserInput(input: Int): Unit

  def setVarControllerState(cs: controllState, s: String): Unit

  def setVarState(state: GameStateInterface): Unit

  def getVarControllerState: (controllState, String)

  def getVarAllowedInput: Vector[String]

  def initialize(playerList: List[PlayerInterface]): GameStateInterface

  def playerAmount(input: String): Int

  def nextPlayer: GameStateInterface

  def getPlayerName: String

  def drawCard: GameStateInterface

  def checkForCard7or15(playedCard: Int): Int

  def makeTurn: GameStateInterface

  def playCard: GameStateInterface

  def playAnotherCard: GameStateInterface

  def playAnotherCard2(otherCard: Int): GameStateInterface

  def undoStep: GameStateInterface

  def redoStep: GameStateInterface

  def playEffect(selectedEffect: Int): GameStateInterface

  def playerChoosed(choosedPlayer: Int): GameStateInterface

  def investgatorGuessed(guess: Int): GameStateInterface

  def checkUponWin: Boolean

  def playerWins(winningPlayer: Int): GameStateInterface

  def getAllowedPlayerForPlayerSelection: Vector[String]

  def rekGetAllowedPlayerForPlayerSelection(
                                             counter: Int,
                                             playerList: List[PlayerInterface],
                                             allowedPlayers: Vector[String]
                                           ): Vector[String]

  def eliminatePlayer(player: Int): GameStateInterface

  def handle: String

  def resetControllerState: Unit

  def save(using fileIO: FileIOInterface): Unit

  def load(using fileIO: FileIOInterface): Unit
}
