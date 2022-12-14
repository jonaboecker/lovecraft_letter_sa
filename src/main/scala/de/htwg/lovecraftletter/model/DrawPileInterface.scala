package de.htwg.lovecraftletter.model

trait DrawPileInterface {

  def newPile: List[Int]

  def drawAndGet(drawPile: List[Int]): (List[Int], Int)

  def startingHands(
                     drawPile: List[Int],
                     playerAmount: Int
                   ): (List[Int], List[Int])

  def rekStartingHands(
                        drawPile: List[Int],
                        playerAmount: Int,
                        returnList: List[Int]
                      ): (List[Int], List[Int])
}
