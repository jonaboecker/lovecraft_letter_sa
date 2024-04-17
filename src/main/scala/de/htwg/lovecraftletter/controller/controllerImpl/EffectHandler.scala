package de.htwg.lovecraftletter
package controller
package controllerImpl

import model.BoardImpl.Board
import model.GameStateInterface
import model.DrawPileImpl.DrawPile
import model._

import scala.util._

class EffectHandler(
    var state: GameStateInterface,
    val selection: Vector[Int]
    // => selectedEffect, chosenPlayer, others
) {
  val contr: ControllerRequestActor = ControllerRequestActor()
  def initializeEffectHandler:GameStateInterface = {
    state.player(state.currentPlayer).discardPile.head match
        case 1|2|3|5|6|9|10|11|13|14 =>
            if(contr.getAllowedPlayerForPlayerSelection.isEmpty) {
                standardOutput("Es kann kein Gegner ausgewaehlt werden")
                return exit
            } else {
                contr.setVarControllerState(controllState.getEffectedPlayer, "")
                contr.notifyObservers()
            }
            state
        case _ => strategy
  }

  def strategy: GameStateInterface = selection(0) match
    case 1 => strategyNormal
    case 2 => strategyMad

  private def strategyNormal: GameStateInterface =
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

  private def strategyMad: GameStateInterface =
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

  private def guessTeammateHandcard: GameStateInterface = {
    if(selection(0) == 2) { //if mad effect
        if(state.player(selection(1)).hand == 1) {
            state = contr.eliminatePlayer(selection(1))
        }
        return exit
    }
    contr.setVarControllerState(controllState.getInvestigatorGuess, "")
    contr.notifyObservers()
    state
  }

  def guessTeammateHandcard2: GameStateInterface = {
    val playerHand = state.player(selection(1)).hand
    val isSpecialCard = selection(2) == 0 && playerHand == 16
    val isMatchingCard = selection(2) != 0 && (playerHand == selection(2) || playerHand == selection(2) + 8)
  
    if (isSpecialCard || isMatchingCard) {
      state = contr.eliminatePlayer(selection(1))
    } else {
      standardOutput("Dein Tipp war leider falsch")
    }
    exit
  }

  private def showTeammateHandcard: GameStateInterface = {
    val output = state.player(selection(1)).name + "s Handkarte ist\n" + Board(1, Vector(state.player(selection(1)).hand),
      0).toString
    standardOutput(output)
    if(selection(0) == 2) { //madcard played
        standardOutput("Du darfst nochmal ziehen")
        state = contr.playAnotherCard()
    }
    exit
  }

  private def compareTeammateHandcard: GameStateInterface = {
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

  private def discardAndDraw: GameStateInterface = {
    val selectedPlayer = state.player(selection(1))
  
    // Handkarte ablegen
    state = state.updatePlayer(state.player.updated(selection(1), selectedPlayer.discardCard(selectedPlayer.hand)))
  
    // Neue Karte ziehen
    val drawPile = new DrawPile
    val (tempDrawPile: List[Int], tempCard: Int) = drawPile.drawAndGet(state.drawPile)
  
    // Draw pile aktualisieren und gezogene Karte als Handkarte setzen
    state = state.updateDrawPile(tempDrawPile)
    state = state.updatePlayer(state.player.updated(selection(1), selectedPlayer.changeHand(tempCard)))
  
    // Zustand synchronisieren
    contr.setVarState(state)
  
    // Überprüfen, ob der Spieler ausscheiden muss
    state = checkPlayerElimination(selectedPlayer)
  
    exit
  }
  
  private def checkPlayerElimination(player: PlayerInterface): GameStateInterface = {
    val discardedCard = player.discardPile.head
  
    if (discardedCard == 8) {
      standardOutput("Necronomicon abgelegt")
      return contr.eliminatePlayer(selection(1))
    }
  
    if (discardedCard == 16) {
      standardOutput("Cthulhu abgelegt")
      if (player.madCheck() >= 2) {
        return contr.playerWins(selection(1))
      } else {
        return contr.eliminatePlayer(selection(1))
      }
    }
  
    state
  }

  private def swapHandcards: GameStateInterface = {
    val tempCard:Int = state.player(state.currentPlayer).hand
    state = state.updatePlayer(state.player.updated(state.currentPlayer, state.player(state.currentPlayer).changeHand(state.player(selection(1)).hand)))
    state = state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).changeHand(tempCard)))
    exit
  }

  private def eliminateMadPlayer: GameStateInterface = {
    if(state.player(selection(1)).madCheck() == 0) {
        state = contr.eliminatePlayer(selection(1))
    }
    exit
  }

  private def playMiGoMad: GameStateInterface = {
    state = state.updateDrawPile(state.player(selection(1)).hand :: state.drawPile)
    state = state.updatePlayer(state.player.updated(selection(1), state.player(selection(1)).changeHand(17)))
    standardOutput("Du darfst nochmal ziehen")
    contr.setVarState(state) //sync gamestate
    state = contr.playAnotherCard()
    exit
  }

  private def playniegrhaasdfsaj: GameStateInterface = {
    val cards = for(p <- state.player) yield p.hand
    val cardsVec:Vector[Int] = Random.shuffle(cards.toVector)
    for(i <- state.player.indices) {
        state = state.updatePlayer(state.player.updated(i, state.player(i).changeHand(cardsVec(i))))
    }
    exit
  }

  private def playTrapezoeder: GameStateInterface = {
    state.player(state.currentPlayer).hand match
        case 5|6|7|8|13|14|16 => state = contr.playerWins(state.currentPlayer)
        case _ =>
    exit
  }

  private def playCthulu: GameStateInterface = {
    if(state.player(state.currentPlayer).madCheck() > 2) {
        state = contr.playerWins(state.currentPlayer)
    } else {
       state = contr.eliminatePlayer(state.currentPlayer)
    }
    exit
  }

  private def standardOutput(output: String): Unit = { // show Effect result to player
    contr.setVarControllerState(controllState.informOverPlayedEffect, output)
    contr.notifyObservers()
    contr.resetControllerState()
  }

  def exit:GameStateInterface = {
    contr.setVarState(state) //sync gamestate
    state = contr.nextPlayer()
    contr.resetControllerState()
    contr.notifyObservers()
    state
  }
}
