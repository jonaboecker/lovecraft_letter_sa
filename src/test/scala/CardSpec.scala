package scala

//import Card.scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._


class CardSpec extends AnyWordSpec with Matchers{


  "A Card" should {
    val defaultCard  = new Card("Test", 8, 5, "Test", "")
    "contain default values" in{
      defaultCard.name should not be ""
      defaultCard.value should be > -1
      defaultCard.value should be < 9
      defaultCard.number should be > 0
      defaultCard.number should be < 6
      defaultCard.effect should not be ""


    }
    "return the correct String" in {
      val defaultCard = new Card("Test", 8, 5, "Test", "")
      val result = defaultCard.fillspace("Test", 2)
      val expected = "Test            "
      result should === (expected)
    }

  }
}
