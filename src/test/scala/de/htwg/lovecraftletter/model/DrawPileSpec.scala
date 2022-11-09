package de.htwg.lovecraftletter
package model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class DrawPileSpec extends AnyWordSpec with Matchers {

  "A DrawPile" should {
    val defaultDrawPile = new DrawPile
    "return the correct DrawPile" in {
      val result = (List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16))
      defaultDrawPile.newPile should contain allElementsOf (result)
      defaultDrawPile.newPile should have length 24
    }
    "return the correct Tuple" in {
      defaultDrawPile.drawAndGet(List(3, 2, 1)) should ===((List(2, 1), 3))
    }
    "return the correct Starting Hands" in {
      defaultDrawPile.startingHands(List(1, 2, 3, 4, 5, 6), 4) should ===(
        (List(5, 6), List(4, 3, 2, 1))
      )
    }
    "return the correct Starting Hands rek" in {
      defaultDrawPile.rekStartingHands(List(1, 2, 3, 4, 5, 6), 4, Nil) === ((
        List(5, 6),
        List(4, 3, 2, 1)
      ))
    }
  }
}
