package de.htwg.lovecraftletter
package model

import scala.util.Random

case class DrawPile() {

  def newPile: List[Int] = {
    Random.shuffle(
      List(1, 1, 1, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 7, 8, 9, 10, 11, 12, 13,
        14, 15, 16)
    )
  }

  def drawAndGet(drawPile: List[Int]): (List[Int], Int) = {
    (drawPile.tail, drawPile.head)
  }

  def startingHands(
      drawPile: List[Int],
      playerAmount: Int
  ): (List[Int], List[Int]) = {
    rekStartingHands(drawPile, playerAmount, Nil)
  }

  def rekStartingHands(
      drawPile: List[Int],
      playerAmount: Int,
      returnList: List[Int]
  ): (List[Int], List[Int]) = {
    val (newDrawPile, tempInt) = drawAndGet(drawPile)
    val tempList = tempInt :: returnList
    if (playerAmount != tempList.length) {
      rekStartingHands(newDrawPile, playerAmount, tempList)
    } else {
      (newDrawPile, tempList)
    }
  }
}
