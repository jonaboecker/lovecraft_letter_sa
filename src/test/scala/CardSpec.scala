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
      defaultCard.getName(1) should ===(nameResult)
    }
    "return the correct Value" in {
      defaultCard.getValue(1) should ===("1")
    }
    "return the correct Amount" in {
      defaultCard.getAmount(1) should ===("5")
    }
    "return the correct Effect" in {
      defaultCard.getEffect(1) should ===("Rate")
    }
    "return the correct MadEffect" in {
      defaultCard.getMadEffect(1) should ===("")
    }
    "return the correct Effect Snippet" in {
      defaultCard.getEffectSnippet(1, 2, 14) should ===("              ")
    }
    "return the correct Title Snippet" in {
      defaultCard.getTitelSnippet(1, 1, 14) should ===("Investigator  ")
    }
  }
}
