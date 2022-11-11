package de.htwg.lovecraftletter.model

case class Player(name: String, hand: Int, discardPile: List[Int]) {
  def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): Player = {
    copy(hand = card, discardPile = newDiscardPile)
  }

  def discardCard(card: Int): Player = {
    Player(name, hand, card :: discardPile)
  }

  def changeHand(card: Int): Player = {
    Player(name, card, discardPile)
  }
}
