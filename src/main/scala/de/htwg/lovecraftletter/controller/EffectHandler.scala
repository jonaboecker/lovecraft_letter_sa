package de.htwg.lovecraftletter
package controller

import model._
import util.Observable

class EffectHandler(val contr: Controller, var state: GameState, var selectedEffect: Int, var userInput: Int) extends Observable {

  def reInitialize(newState: GameState, newSelectedEffect: Int):EffectHandler = {
    state = newState
    selectedEffect = newSelectedEffect
    userInput = -999
    this
  }

  def strategy:GameState = selectedEffect match
    case 1 => strategyNormal
    case 2 => strategyMad

  def strategyNormal:GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 1 | 9  => playInvestigator
      case 2 | 10 => state
      case 3 | 11 => state
      case 4 | 12 => state
      case 5 | 13 => state
      case 6 | 14 => state
      case 7 | 15 => state
      case 8 | 16 => state

  def strategyMad:GameState = state.player(state.currentPlayer).discardPile.head match
    case 9  => state
    case 10 => state
    case 11 => state
    case 12 => state
    case 13 => state
    case 14 => state
    case 15 => state
    case 16 => state

  def setUserInput(input:Int):Int = {
    userInput = input
    print("input settet with " + userInput)
    userInput
  }

  def playInvestigator:GameState = {
    val choosedPlayer:Int = choosePlayer
    println(choosedPlayer)
    println(state.player(choosedPlayer).hand)
    contr.controllerState = (controllState.getInvestigatorGuess, "")
    notifyObservers
    contr.resetControllerState
    //Wert der Handkarte bei 0-8 = enum, ansonsten input + 8, Blank zählt nicht
    if(userInput == 0 && state.player(choosedPlayer).hand == 16) {
        contr.eliminatePlayer(choosedPlayer)
        syncState
    } else if(userInput != 0 && (state.player(choosedPlayer).hand == userInput || state.player(choosedPlayer).hand == userInput + 8)) {
        contr.eliminatePlayer(choosedPlayer)
        syncState
    } else {
        contr.controllerState = (controllState.informOverPlayedEffect, "Dein Tipp war leider falsch")
        notifyObservers
        contr.resetControllerState
    }
    state
  }
  
  def choosePlayer:Int = { //get Player for handle effect to
    contr.controllerState = (controllState.getEffectedPlayer, "")
    notifyObservers
    println("no")
    contr.resetControllerState
    userInput - 1
  }

  def syncState = {
    state = contr.state
  }

}
