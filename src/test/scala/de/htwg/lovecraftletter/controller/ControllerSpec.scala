package de.htwg.lovecraftletter
package controller

import model._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class ControllerSpec extends AnyWordSpec with Matchers {
  "A Controller" should {
    "return the correct initial state" in {
      val defaultController = new Controller(GameState(0, Nil, Nil, 0),"standard")
      val result: GameState = defaultController.initialize(
        List(
          Player("Gustav", 0, Nil, true),
          Player("Guschtav", 0, Nil, true),
          Player("Guschtav", 0, Nil, true)
        )
      )
      result.currentPlayer should ===(0)
      result.drawPile should not be empty
      result.player should not be empty
      result.currentCard should be > 0

    }
    "return the correct PlayerAmmount" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ),"standard"
      )
      defaultController.playerAmount("3") should ===(3)
      defaultController.playerAmount("4") should ===(4)
      defaultController.playerAmount("5") should ===(5)
      defaultController.playerAmount("6") should ===(6)
      defaultController.playerAmount("ljasdvnluid") should ===(0)
    }
    "return the correct Board String" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ), "standard"
      )
      val board = Board(3, Vector(1, 1, 0), 1)
      defaultController.StateHandler.getBoard should ===("\nGuschtav ist an der Reihe\n" + board.toString + "\nWelche Karte moechtest du spielen? (1|2)")
    }
    "return correct next Player" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ), "standard"
      )
      val result = GameState(
        0,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), true)
        ),
        1
      )
      defaultController.nextPlayer should ===(result)
    }
    "return correct PlayerName" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ), "standard"
      )
      defaultController.getPlayerName should ===("Guschtav")
    }
    "return the correct drawed Card and Discard Pile" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ), "standard"
      )
      defaultController.drawCard should ===(
        GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          2
        )
      )
    }
    "return correct GameState" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ), "standard"
      )
      defaultController.playCard(1) should ===(
        GameState(
          0,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(1, 0), true)
          ),
          2
        )
      )

      val defaultState2 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 5, List(0), true)
        ),
        1
      )
      val defaultController2 = Controller(defaultState2, "standard")
      defaultController2.playCard(2) should ===(
        GameState(
          0,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          2
        )
      )
    }
  }
}
