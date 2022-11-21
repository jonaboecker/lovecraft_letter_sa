package de.htwg.lovecraftletter.model

case class Player(
    name: String,
    hand: Int,
    discardPile: List[Int],
    inGame: Boolean
) {
  def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): Player = {
    copy(hand = card, discardPile = newDiscardPile, inGame)
  }

  def discardCard(card: Int): Player = {
    Player(name, hand, card :: discardPile, inGame)
  }

  def changeHand(card: Int): Player = {
    Player(name, card, discardPile, inGame)
  }

  def eliminatePlayer(): Player = {
    Player(name, hand, discardPile, false)
  }

  def madCheck(): Int = {
    discardPile.filter(_ > 8).length
  }
}
