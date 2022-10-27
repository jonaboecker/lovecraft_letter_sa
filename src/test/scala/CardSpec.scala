package scala

//import Card.scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class CardSpec extends AnyWordSpec with Matchers {

  "A Card" should {
    "return the correct String" in {
      Card.getName(1) === ("Investigator")
      Card.getValue(1) === ("1")
      Card.getAmount(1) === ("5")
      Card.getEffect(1) === ("Rate")
      Card.getMadEffect(1) === ("")
    }
  }
}
