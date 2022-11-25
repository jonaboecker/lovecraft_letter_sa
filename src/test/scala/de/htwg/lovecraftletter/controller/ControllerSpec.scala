package de.htwg.lovecraftletter
package controller

import model._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class ControllerSpec extends AnyWordSpec with Matchers {
  "A Controller" should {
    "return the correct initial state" in {
      val defaultController =
        new Controller(GameState(0, Nil, Nil, 0), Vector("standard"))
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
        ),
        Vector("standard")
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
        ),
        Vector("standard")
      )
      val board = Board(3, Vector(1, 1, 0), 1)
      defaultController.StateHandler.getBoard should ===(
        "\nGuschtav ist an der Reihe\n" + board.toString + "\nWelche Karte moechtest du spielen? (1|2)"
      )
    }
    "return correct next Player" in {
      // test if all player in Game
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ),
        Vector("standard")
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
      // test if a player is out
      val defaultController2 = new Controller(
        GameState(
          0,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), false),
            Player("Player 3", 1, List(0), true)
          ),
          1
        ),
        Vector("standard")
      )
      val result2 = GameState(
        2,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false),
          Player("Player 3", 1, List(0), true)
        ),
        1
      )
      defaultController2.nextPlayer should ===(result2)
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
        ),
        Vector("standard")
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
        ),
        Vector("standard")
      )
      defaultController.MadHandler.draw should ===(
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
    "return the correct state after draw card if player is mad" in {
      // not eliminate
      val defaultController = new Controller(
        GameState(
          0,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(10, 0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          0
        ),
        Vector("standard")
      )
      defaultController.MadHandler.draw should ===(
        GameState(
          0,
          List(4, 5, 1),
          List(
            Player("Gustav", 1, List(2, 10, 0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          3
        )
      )
      // eliminate player
      val defaultController2 = new Controller(
        GameState(
          0,
          List(11, 2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(10, 0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          0
        ),
        Vector("standard")
      )
      defaultController2.MadHandler.draw should ===(
        GameState(
          0,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(11, 10, 0), false),
            Player("Guschtav", 1, List(0), true)
          ),
          0
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
        ),
        Vector("standard")
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
      val defaultController2 = Controller(defaultState2, Vector("standard"))
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

      val defaultState3 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 5, List(9, 0), true)
        ),
        10
      )
      val defaultController3 = Controller(defaultState3, Vector("standard"))
      defaultController3.playCard(1) should ===(
        GameState(
          0,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 5, List(10, 9, 0), true)
          ),
          2
        )
      )

      val defaultState4 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 5, List(9, 0), true)
        ),
        1
      )
      val defaultController4 = Controller(defaultState4, Vector("standard"))
      defaultController4.playCard(1) should ===(
        GameState(
          0,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 5, List(1, 9, 0), true)
          ),
          2
        )
      )
    }
    "return correct Boolean for checkUponWin" in {
      val defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), false)
          ),
          1
        ),
        Vector("standard")
      )
      defaultController.checkUponWin should ===(true)
      val defaultController2 = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ),
        Vector("standard")
      )
      defaultController2.checkUponWin should ===(false)
    }
  }
  "State Handler should return correct string" in {
    val defaultController = new Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      Vector("standard")
    )
    defaultController.StateHandler.handle should ===(
      defaultController.StateHandler.getBoard
    )
    val defaultController2 = new Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      Vector("standard")
    )
    defaultController2.StateHandler.handle should ===(
      defaultController2.StateHandler.selectEffect
    )
  }
}
