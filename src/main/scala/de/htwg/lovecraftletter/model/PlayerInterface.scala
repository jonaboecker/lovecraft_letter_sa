package de.htwg.lovecraftletter.model

trait PlayerInterface(
                   val name: String,
                   val hand: Int,
                   val discardPile: List[Int],
                   val inGame: Boolean
                 ) {

  def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): PlayerInterface

  def discardCard(card: Int): PlayerInterface

  def changeHand(card: Int): PlayerInterface

  def eliminatePlayer(): PlayerInterface

  def madCheck(): Int
  
  def isPlayerMad: Boolean
}
