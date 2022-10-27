package scala

//import Card.scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    val defaultCard = new Card
    "return the correct String" in {
      defaultCard.getName(1) === ("Investigatoren")
      defaultCard.getValue(1) === ("1")
      defaultCard.getAmount(1) === ("5")
      defaultCard.getEffect(1) === ("Rate")
      defaultCard.getMadEffect(1) === ("")
      defaultCard.getEffectSnippet(1, 2, 14) === ("              ")
      defaultCard.getTitelSnippet(1, 2, 14) === (" ")
    }
  }
}
