package de.htwg.lovecraftletter
package controller
package controllerImpl

import model.BoardImpl.Board
import model.GameStateInterface
import model.DrawPileImpl.DrawPile
import model._

import scala.util._

class EffectHandler(
    val contr: ControllerInterface,
    var state: GameStateInterface,
    val selection: Vector[Int]
    // => selectedEffect, choosedPlayer, others
) {
  def initializeEffectHandler:GameStateInterface = {
    state.player(state.currentPlayer).discardPile.head match
        case 1|2|3|5|6|9|10|11|13|14 =>
            if(contr.getAllowedPlayerForPlayerSelection.isEmpty) {
                standardOutput("Es kann kein Gegner ausgewaehlt werden")
                return exit
            } else {
                contr.setVarControllerState(controllState.getEffectedPlayer, "")
                contr.notifyObservers
            }
            state
        case _ => strategy
  }

  def strategy: GameStateInterface = selection(0) match
    case 1 => strategyNormal
    case 2 => strategyMad

  def strategyNormal: GameStateInterface =
    state.player(state.currentPlayer).discardPile.head match
      case 1 | 9  => guessTeammateHandcard
      case 2 | 10 => showTeammateHandcard
      case 3 | 11 => compareTeammateHandcard
      case 4 | 12 =>
        standardOutput("Du bist bis zu deinem naechsten Zug geschuetzt")
        exit
        state
      case 5 | 13 =>
        discardAndDraw
      case 6 | 14 => swapHandcards
      case 7 | 15 =>
        exit
        state //implemented in controller
      case 8 | 16 | 17 =>
        state = contr.eliminatePlayer(state.currentPlayer)
        exit

  def strategyMad: GameStateInterface =
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

  def guessTeammateHandcard: GameStateInterface = {
    if(selection(0) == 2) { //if mad effect
        if(state.player(selection(1)).hand == 1) {
            state = contr.eliminatePlayer(selection(1))
        }
        return exit
    }
    contr.setVarControllerState(controllState.getInvestigatorGuess, "")
    contr.notifyObservers
    state
  }

  def guessTeammateHandcard2: GameStateInterface = {
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

  def showTeammateHandcard: GameStateInterface = {
    val output = state.player(selection(1)).name + "s Handkarte ist\n" + Board(1, Vector(state.player(selection(1)).hand),
      0).toString
    standardOutput(output)
    if(selection(0) == 2) { //madcard played
        standardOutput("Du darfst nochmal ziehen")
        state = contr.playAnotherCard
    }
    exit
  }

  def compareTeammateHandcard: GameStateInterface = {
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

  def discardAndDraw: GameStateInterface = {
    //Handkarte ablegen
    state = state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).discardCard(state.player(selection(1)).hand)))
    //neue Karte ziehen
    val drawPile = new DrawPile
    val (tempDrawPile: List[Int], tempCard: Int) = drawPile.drawAndGet(state.drawPile)
    //draw pile aktualisieren und gezogene Karte als Handkarte setzen
    state =  state.updateDrawPile(tempDrawPile)
    state =  state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).changeHand(tempCard)))
    //if necrominikom oder kathulu -> ausscheiden oder gewinnen
    contr.setVarState(state) //sync gamestate
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

  def swapHandcards: GameStateInterface = {
    val tempCard:Int = state.player(state.currentPlayer).hand
    state = state.updatePlayer(state.player.updated(state.currentPlayer, state.player(state.currentPlayer).changeHand(state.player(selection(1)).hand)))
    state = state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).changeHand(tempCard)))
    exit
  }

  def eliminateMadPlayer: GameStateInterface = {
    if(state.player(selection(1)).madCheck() == 0) {
        state = contr.eliminatePlayer(selection(1))
    }
    exit
  }

  def playMiGoMad: GameStateInterface = {
    state = state.updateDrawPile(state.player(selection(1)).hand :: state.drawPile)
    state = state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).changeHand(17)))
    standardOutput("Du darfst nochmal ziehen")
    contr.setVarState(state) //sync gamestate
    state = contr.playAnotherCard
    exit
  }

  def playniegrhaasdfsaj: GameStateInterface = {
    val cards = for(p <- state.player) yield (p.hand)
    val cardsVec:Vector[Int] = Random.shuffle(cards.toVector)
    for(i <- 0 until state.player.length) {
        state = state.updatePlayer(state.player.updated(i, state.player(i).changeHand(cardsVec(i))))
    }
    exit
  }

  def playTrapezoeder: GameStateInterface = {
    state.player(state.currentPlayer).hand match
        case 5|6|7|8|13|14|16 => state = contr.playerWins(state.currentPlayer)
        case _ =>
    exit
  }

  def playCthulu: GameStateInterface = {
    if(state.player(state.currentPlayer).madCheck() > 2) {
        state = contr.playerWins(state.currentPlayer)
    } else {
       state = contr.eliminatePlayer(state.currentPlayer)
    }
    exit
  }

  def standardOutput(output: String) = { // show Effect result to player
    contr.setVarControllerState(controllState.informOverPlayedEffect, output)
    contr.notifyObservers
    contr.resetControllerState()
  }

  def exit:GameStateInterface = {
    contr.setVarState(state) //sync gamestate
    state = contr.nextPlayer
    contr.resetControllerState()
    contr.notifyObservers
    state
  }
}
