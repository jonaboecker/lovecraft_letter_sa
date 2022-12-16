package de.htwg.lovecraftletter.model.PlayerImpl

import de.htwg.lovecraftletter.model.PlayerInterface

case class Player(
                   override val name: String,
                   override val hand: Int,
                   override val discardPile: List[Int],
                   override val inGame: Boolean
                 ) extends PlayerInterface (name, hand, discardPile, inGame) {

  override def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): Player = {
    copy(hand = card, discardPile = newDiscardPile, inGame)
  }

  override def discardCard(card: Int): Player = {
    Player(name, hand, card :: discardPile, inGame)
  }

  override def changeHand(card: Int): Player = {
    Player(name, card, discardPile, inGame)
  }

  override def eliminatePlayer(): Player = {
    Player(name, hand, discardPile, false)
  }

  override def madCheck(): Int = {
    discardPile.filter(_ > 8).length
  }
}
