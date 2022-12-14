package de.htwg.lovecraftletter.model

case class Player(
                   name: String,
                   hand: Int,
                   discardPile: List[Int],
                   inGame: Boolean
                 ) extends PlayerInterface {
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
