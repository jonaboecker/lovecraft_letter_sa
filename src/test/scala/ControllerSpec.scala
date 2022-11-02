package scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._


class ControllerSpec extends AnyWordSpec with Matchers{
  "A Controller" should {
    val defaultController = new Controller
    "return the correct PlayerList" in {
    //   val result = (List(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 13, 14, 15, 16))
    //   val player:List[Player] = List(Player("Tobi"), Player("Jona"))
    //   defaultController.legeSpielerAn should === ((player,List(List(0))))
    }
    "return the correct Ablagestapel" in {
        val result = List(List(0),List(0),List(0))
        defaultController.rekLegeAblagestapelAn(Nil, 3) should === (result)
    }
    "return correct discardPile" in{
        defaultController.playCard(2,1,List(List(0),List(0),List(0))) should === (List(List(0),List(0),List(1,0)))
    }
  }
}
