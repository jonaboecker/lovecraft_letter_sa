package de.htwg.lovecraftletter.model

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class GameStateSpec extends AnyWordSpec with Matchers {
  "A GameState" should {
    val defaultState = new GameState(
      1,
      List(2, 3, 4, 5, 1),
      List(Player("Guschtav", 1, List(0)), Player("Guschtav", 1, List(0))),
      1
    )
    "return the correct Player" in {
      defaultState.nextPlayer should ===(
        GameState(
          0,
          List(2, 3, 4, 5, 1),
          List(Player("Guschtav", 1, List(0)), Player("Guschtav", 1, List(0))),
          1
        )
      )
    }
    "return the correct drawed Card and Discard Pile" in {
      defaultState.drawCard should ===(
        GameState(
          1,
          List(3, 4, 5, 1),
          List(Player("Guschtav", 1, List(0)), Player("Guschtav", 1, List(0))),
          2
        )
      )
    }
    "return correct discardPile" in {
      defaultState.playCard should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Guschtav", 1, List(0)),
            Player("Guschtav", 1, List(1, 0))
          ),
          0
        )
      )
    }
    "return correct swaped Cards" in {
      val defaultState2 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(Player("Guschtav", 1, List(0)), Player("Guschtav", 5, List(0))),
        1
      )
      defaultState2.swapHandAndCurrent should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Guschtav", 1, List(0)),
            Player("Guschtav", 1, List(0))
          ),
          5
        )
      )
    }
  }
}
