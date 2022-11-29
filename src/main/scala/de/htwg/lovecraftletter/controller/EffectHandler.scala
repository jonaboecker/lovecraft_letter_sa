package de.htwg.lovecraftletter
package controller

import model._
import util.Observable

class EffectHandler(
    val contr: Controller,
    var state: GameState,
    var selectedEffect: Int
) {

  def strategy: GameState = selectedEffect match
    case 1 => strategyNormal
    case 2 => strategyMad

  def strategyNormal: GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 1 | 9  => guessTeammateHandcard
      case 2 | 10 => showTeammateHandcard
      case 3 | 11 => compareTeammateHandcard
      case 4 | 12 => state
      case 5 | 13 => state
      case 6 | 14 => state
      case 7 | 15 => state
      case 8 | 16 => state

  def strategyMad: GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 9  => state
      case 10 => state
      case 11 => state
      case 12 => state
      case 13 => state
      case 14 => state
      case 15 => state
      case 16 => state

  def guessTeammateHandcard: GameState = {
    val choosedPlayer: Int = choosePlayer
    // println(choosedPlayer)
    // println(state.player(choosedPlayer).hand)
    contr.controllerState = (controllState.getInvestigatorGuess, "")
    contr.notifyObservers
    contr.resetControllerState
    // Wert der Handkarte bei 0-8 = enum, ansonsten input + 8, Blank zählt nicht
    if (contr.userInput == 0 && state.player(choosedPlayer).hand == 16) {
      state = contr.eliminatePlayer(choosedPlayer)
    } else if (
      contr.userInput != 0 && (state
        .player(choosedPlayer)
        .hand == contr.userInput || state
        .player(choosedPlayer)
        .hand == contr.userInput + 8)
    ) {
      state = contr.eliminatePlayer(choosedPlayer)
    } else {
      standardOutput("Dein Tipp war leider falsch")
    }
    state
  }

  def showTeammateHandcard: GameState = {
    val choosedPlayer: Int = choosePlayer
    val output = state.player(choosedPlayer).name + "s Handkarte ist\n" + Board(
      1,
      Vector(state.player(choosedPlayer).hand),
      0
    ).toString
    standardOutput(output)
    state
  }

  def compareTeammateHandcard: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (
      state.player(choosedPlayer).hand > state.player(state.currentPlayer).hand
    ) {
      state = contr.eliminatePlayer(state.currentPlayer)
    } else if (
      state.player(choosedPlayer).hand < state.player(state.currentPlayer).hand
    ) {
      state = contr.eliminatePlayer(choosedPlayer)
    }
    state
  }

  def choosePlayer: Int = { // get Player for handle effect to
    contr.controllerState = (controllState.getEffectedPlayer, "")
    // println("no")
    contr.notifyObservers
    contr.resetControllerState
    contr.userInput - 1
  }

  def standardOutput(output: String) = { // show Effect result to player
    contr.controllerState = (controllState.informOverPlayedEffect, output)
    contr.notifyObservers
    contr.resetControllerState
  }
}
