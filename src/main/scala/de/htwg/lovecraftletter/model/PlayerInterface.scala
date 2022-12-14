package de.htwg.lovecraftletter.model

trait PlayerInterface {

  def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): Player

  def discardCard(card: Int): Player

  def changeHand(card: Int): Player

  def eliminatePlayer(): Player

  def madCheck(): Int
}
