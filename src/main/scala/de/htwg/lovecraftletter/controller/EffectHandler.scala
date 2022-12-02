package de.htwg.lovecraftletter
package controller

import model._
import util.Observable

import scala.util._

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
      case 4 | 12 => 
        standardOutput("Du bist bis zu deinem naechsten Zug geschuetzt")
        state
      case 5 | 13 => discardAndDraw
      case 6 | 14 => swapHandcards
      case 7 | 15 => state //implemented in controller
      case 8 | 16 | 17 => contr.eliminatePlayer(state.currentPlayer)

  def strategyMad: GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 9  => guessTeammateHandcard
      case 10 => showTeammateHandcard
      case 11 => eliminateMadPlayer
      case 12 => 
        standardOutput("Du bist bis zur naechsten Runde geschuetzt")
        state
      case 13 => playMiGoMad
      case 14 => playniegrhaasdfsaj
      case 15 => playTrapezoeder
      case 16 => playCthulu
      case 17 => contr.eliminatePlayer(state.currentPlayer)

  def guessTeammateHandcard: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
    if(selectedEffect == 2) { //if mad effect
        if(state.player(choosedPlayer).hand == 1) {
            state = contr.eliminatePlayer(choosedPlayer)
        }
        return state
    }
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
    if (choosedPlayer == -1) return state
    val output = state.player(choosedPlayer).name + "s Handkarte ist\n" + Board(
      1,
      Vector(state.player(choosedPlayer).hand),
      0
    ).toString
    standardOutput(output)
    if(selectedEffect == 2) { //madcard played
        standardOutput("Du darfst nochmal ziehen")
        state = contr.playAnotherCard
    }
    state
  }

  def compareTeammateHandcard: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
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

  def discardAndDraw: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
    //Handkarte ablegen
    state.player = state.player.updated(choosedPlayer, state.player(choosedPlayer).discardCard(state.player(choosedPlayer).hand))
    //neue Karte ziehen
    val drawPile = DrawPile()
    val (tempDrawPile: List[Int], tempCard: Int) = drawPile.drawAndGet(state.drawPile)
    //draw pile aktualisieren und gezogene Karte als Handkarte setzen
    state.drawPile = tempDrawPile
    state.player = state.player.updated(choosedPlayer, state.player(choosedPlayer).changeHand(tempCard))
    //if necrominikom oder kathulu -> ausscheiden oder gewinnen
    if(state.player(choosedPlayer).discardPile.head == 8) {
        standardOutput("Necronomicon abgelegt")
        state = contr.eliminatePlayer(choosedPlayer)
    }
    if(state.player(choosedPlayer).discardPile.head == 16) {
        standardOutput("Cthulhu abgelegt")
        if(state.player(choosedPlayer).madCheck() >= 2) {
            state = contr.playerWins(choosedPlayer)
        } else {
            state = contr.eliminatePlayer(choosedPlayer)
        }
    }
    state
  }

  def swapHandcards: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
    val tempCard:Int = state.player(state.currentPlayer).hand
    state.player = state.player.updated(state.currentPlayer, state.player(state.currentPlayer).changeHand(state.player(choosedPlayer).hand))
    state.player = state.player.updated(choosedPlayer, state.player(choosedPlayer).changeHand(tempCard))
    state
  }

  def eliminateMadPlayer: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
    if(state.player(choosedPlayer).madCheck() == 0) {
        state = contr.eliminatePlayer(choosedPlayer)
    }
    state
  }

  def playMiGoMad: GameState = {
    val choosedPlayer: Int = choosePlayer
    if (choosedPlayer == -1) return state
    state.drawPile = state.player(choosedPlayer).hand :: state.drawPile
    state.player = state.player.updated(choosedPlayer, state.player(choosedPlayer).changeHand(17))
    standardOutput("Du darfst nochmal ziehen")
    state = contr.playAnotherCard
    state
  }

  def playniegrhaasdfsaj: GameState = {
    val cards = for(p <- state.player) yield (p.hand)
    val cardsVec:Vector[Int] = Random.shuffle(cards.toVector)
    for(i <- 0 until state.player.length) {
        state.player = state.player.updated(i, state.player(i).changeHand(cardsVec(i)))
    }
    state
  }

  def playTrapezoeder: GameState = {
    state.player(state.currentPlayer).hand match 
        case 5|6|7|8|13|14|16 => state = contr.playerWins(state.currentPlayer)
        case _ =>
    state
  }

  def playCthulu: GameState = {
    if(state.player(state.currentPlayer).madCheck() > 2) {
        state = contr.playerWins(state.currentPlayer)
    } else {
       state = contr.eliminatePlayer(state.currentPlayer) 
    }
    state
  }

  def choosePlayer: Int = { // get Player for handle effect to
    if(contr.getAllowedPlayerForPlayerSelection.isEmpty) {
        standardOutput("Es kann kein Gegner ausgewaehlt werden")
        return -1
    }
    contr.controllerState = (controllState.getEffectedPlayer, "")
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
