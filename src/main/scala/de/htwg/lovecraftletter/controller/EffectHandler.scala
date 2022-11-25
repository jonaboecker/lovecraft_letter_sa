package de.htwg.lovecraftletter
package controller

import model._

class EffectHandler(var state: GameState, val selectedEffect: Int) {

  def strategy = selectedEffect match
    case 1 => strategyNormal
    case 2 => strategyMad

  def strategyNormal =
    state.player(state.currentPlayer).discardPile.head match
      case 1 | 9  => println("Choose Player")
      case 2 | 10 =>
      case 3 | 11 =>
      case 4 | 12 =>
      case 5 | 13 =>
      case 6 | 14 =>
      case 7 | 15 =>
      case 8 | 16 =>

  def strategyMad = state.player(state.currentPlayer).discardPile.head match
    case 9  =>
    case 10 =>
    case 11 =>
    case 12 =>
    case 13 =>
    case 14 =>
    case 15 =>
    case 16 =>

}
