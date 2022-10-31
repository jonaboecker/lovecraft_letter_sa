package scala

//import Card.scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    val defaultCard = new Card
    "return the correct Name" in {
      val nameResult = ("Investigator")
      defaultCard.cards.fromOrdinal(1).toString() should ===(nameResult)
    }
    "return the correct Value" in {
      defaultCard.cards.fromOrdinal(1).getValue should ===("1")
    }
    "return the correct Amount" in {
      defaultCard.cards.fromOrdinal(1).getAmount should ===("5")
    }
    "return the correct Effect" in {
      defaultCard.cards.fromOrdinal(1).getEffect should ===("Rate")
    }
    "return the correct MadEffect" in {
      defaultCard.cards.fromOrdinal(1).getMadEffect should ===("")
    }
    "return the correct Effect Snippet" in {
      defaultCard.getEffectSnippet(1, 2, 14) should ===("              ")
    }
    "return the correct Title Snippet" in {
      defaultCard.getTitelSnippet(1, 1, 14) should ===("Investigator  ")
    }
    "return the correct String for fillspace" in {
      defaultCard.fillspace("Hallo", 10) should ===("Hallo     ")
    }
  }
}
