package de.htwg.lovecraftletter
package controller

import model._
import util.Observable

import scala.util._

class EffectHandler(
    val contr: Controller,
    var state: GameState,
    val selection: Vector[Int]
    // => selectedEffect, choosedPlayer, others
) {
  def initializeEffectHandler:GameState = {
    state.player(state.currentPlayer).discardPile.head match
        case 1|2|3|5|6|9|10|11|13|14 => 
            if(contr.getAllowedPlayerForPlayerSelection.isEmpty) {
                standardOutput("Es kann kein Gegner ausgewaehlt werden")
            } else {
                contr.controllerState = (controllState.getEffectedPlayer, "")
                contr.notifyObservers
            }
            state
        case _ => strategy
  }

  def strategy: GameState = selection(0) match
    case 1 => strategyNormal
    case 2 => strategyMad

  def strategyNormal: GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 1 | 9  => guessTeammateHandcard
      case 2 | 10 => showTeammateHandcard
      case 3 | 11 => compareTeammateHandcard
      case 4 | 12 => 
        standardOutput("Du bist bis zu deinem naechsten Zug geschuetzt")
        exit
        state
      case 5 | 13 => discardAndDraw
      case 6 | 14 => swapHandcards
      case 7 | 15 => 
        exit
        state //implemented in controller
      case 8 | 16 | 17 => 
        contr.eliminatePlayer(state.currentPlayer)
        exit

  def strategyMad: GameState =
    state.player(state.currentPlayer).discardPile.head match
      case 9  => guessTeammateHandcard
      case 10 => showTeammateHandcard
      case 11 => eliminateMadPlayer
      case 12 => 
        standardOutput("Du bist bis zur naechsten Runde geschuetzt")
        exit
        state
      case 13 => playMiGoMad
      case 14 => playniegrhaasdfsaj
      case 15 => playTrapezoeder
      case 16 => playCthulu
      case 17 => 
        state = contr.eliminatePlayer(state.currentPlayer)
        exit

  def guessTeammateHandcard: GameState = {
    if(selection(0) == 2) { //if mad effect
        if(state.player(selection(1)).hand == 1) {
            state = contr.eliminatePlayer(selection(1))
        }
        return exit
    }
    contr.controllerState = (controllState.getInvestigatorGuess, "")
    contr.notifyObservers
    state
  }

  def guessTeammateHandcard2: GameState = {
    // Wert der Handkarte bei 0-8 = enum, ansonsten input + 8, Blank zählt nicht
    if (selection(2) == 0 && state.player(selection(1)).hand == 16) {
      state = contr.eliminatePlayer(selection(1))
    } else if (
      selection(2) != 0 && (state
        .player(selection(1))
        .hand == selection(2) || state
        .player(selection(1))
        .hand == selection(2) + 8)
    ) {
      state = contr.eliminatePlayer(selection(1))
    } else {
      standardOutput("Dein Tipp war leider falsch")
    }
    exit
  }

  def showTeammateHandcard: GameState = {
    val output = state.player(selection(1)).name + "s Handkarte ist\n" + Board(
      1,
      Vector(state.player(selection(1)).hand),
      0
    ).toString
    standardOutput(output)
    if(selection(0) == 2) { //madcard played
        standardOutput("Du darfst nochmal ziehen")
        state = contr.playAnotherCard
    }
    exit
  }

  def compareTeammateHandcard: GameState = {
    if (
      state.player(selection(1)).hand > state.player(state.currentPlayer).hand
    ) {
      state = contr.eliminatePlayer(state.currentPlayer)
    } else if (
      state.player(selection(1)).hand < state.player(state.currentPlayer).hand
    ) {
      state = contr.eliminatePlayer(selection(1))
    }
    exit
  }

  def discardAndDraw: GameState = {
    //Handkarte ablegen
    state.player = state.player.updated(selection(1), state.player(selection(1)).discardCard(state.player(selection(1)).hand))
    //neue Karte ziehen
    val drawPile = DrawPile()
    val (tempDrawPile: List[Int], tempCard: Int) = drawPile.drawAndGet(state.drawPile)
    //draw pile aktualisieren und gezogene Karte als Handkarte setzen
    state.drawPile = tempDrawPile
    state.player = state.player.updated(selection(1), state.player(selection(1)).changeHand(tempCard))
    //if necrominikom oder kathulu -> ausscheiden oder gewinnen
    if(state.player(selection(1)).discardPile.head == 8) {
        standardOutput("Necronomicon abgelegt")
        state = contr.eliminatePlayer(selection(1))
    }
    if(state.player(selection(1)).discardPile.head == 16) {
        standardOutput("Cthulhu abgelegt")
        if(state.player(selection(1)).madCheck() >= 2) {
            state = contr.playerWins(selection(1))
        } else {
            state = contr.eliminatePlayer(selection(1))
        }
    }
    exit
  }

  def swapHandcards: GameState = {
    val tempCard:Int = state.player(state.currentPlayer).hand
    state.player = state.player.updated(state.currentPlayer, state.player(state.currentPlayer).changeHand(state.player(selection(1)).hand))
    state.player = state.player.updated(selection(1), state.player(selection(1)).changeHand(tempCard))
    exit
  }

  def eliminateMadPlayer: GameState = {
    if(state.player(selection(1)).madCheck() == 0) {
        state = contr.eliminatePlayer(selection(1))
    }
    exit
  }

  def playMiGoMad: GameState = {
    state.drawPile = state.player(selection(1)).hand :: state.drawPile
    state.player = state.player.updated(selection(1), state.player(selection(1)).changeHand(17))
    standardOutput("Du darfst nochmal ziehen")
    state = contr.playAnotherCard
    exit
  }

  def playniegrhaasdfsaj: GameState = {
    val cards = for(p <- state.player) yield (p.hand)
    val cardsVec:Vector[Int] = Random.shuffle(cards.toVector)
    for(i <- 0 until state.player.length) {
        state.player = state.player.updated(i, state.player(i).changeHand(cardsVec(i)))
    }
    exit
  }

  def playTrapezoeder: GameState = {
    state.player(state.currentPlayer).hand match 
        case 5|6|7|8|13|14|16 => state = contr.playerWins(state.currentPlayer)
        case _ =>
    exit
  }

  def playCthulu: GameState = {
    if(state.player(state.currentPlayer).madCheck() > 2) {
        state = contr.playerWins(state.currentPlayer)
    } else {
       state = contr.eliminatePlayer(state.currentPlayer) 
    }
    exit
  }

  def standardOutput(output: String) = { // show Effect result to player
    contr.controllerState = (controllState.informOverPlayedEffect, output)
    contr.notifyObservers
    contr.resetControllerState
  }

  def exit:GameState = {
    state = contr.nextPlayer
    contr.resetControllerState
    contr.notifyObservers
    state
  }
}
