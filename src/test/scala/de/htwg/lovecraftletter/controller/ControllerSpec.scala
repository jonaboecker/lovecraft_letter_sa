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
        new Controller(
          GameState(0, Nil, Nil, 0),
          (controllState.standard, ""),
          -2
        )
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
        (controllState.standard, ""),
        -2
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
        (controllState.standard, ""),
        -2
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
        (controllState.standard, ""),
        -2
      )
      val result = GameState(
        0,
        List(3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), true)
        ),
        2
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
        (controllState.standard, ""),
        -2
      )
      val result2 = GameState(
        2,
        List(3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false),
          Player("Player 3", 1, List(0), true)
        ),
        2
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
        (controllState.standard, ""),
        -2
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
        (controllState.standard, ""),
        -2
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
        (controllState.standard, ""),
        -2
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
            Player("Guschtav", 1, List(0), true),
            Player("P3", 1, List(0), true)
          ),
          0
        ),
        (controllState.standard, ""),
        -2
      )
      defaultController2.MadHandler.draw should ===(
        GameState(
          1,
          List(4, 5, 1),
          List(
            Player("Gustav", 1, List(10, 0), false),
            Player("Guschtav", 1, List(0), true),
            Player("P3", 1, List(0), true)
          ),
          3
        )
      )

      // eliminate player at first cardcheck
      val defaultController3 = new Controller(
        GameState(
          0,
          List(11, 2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(13, 10, 0), true),
            Player("Guschtav", 1, List(0), true),
            Player("P3", 1, List(0), true)
          ),
          0
        ),
        (controllState.standard, ""),
        -2
      )
      defaultController3.MadHandler.draw should ===(
        GameState(
          1,
          List(4, 5, 1),
          List(Player("Gustav", 1, List(13, 10, 0), false), Player("Guschtav", 1, List(0), true), Player("P3", 1, List(0), true)),
          3
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
          2
        ),
        (controllState.standard, ""),
        1
      )
      defaultController.playCard should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(2, 0), true)
          ),
          0
        )
      )

      val defaultState2 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 2, List(0), true)
        ),
        1
      )
      val defaultController2 =
        Controller(defaultState2, (controllState.standard, ""), 2)
      defaultController2.playCard should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(2, 0), true)
          ),
          0
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
      val defaultController3 =
        Controller(defaultState3, (controllState.standard, ""), 1)
      defaultController3.playCard should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 5, List(10, 9, 0), true)
          ),
          0
        )
      )

      val defaultState4 = new GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 5, List(9, 0), true)
        ),
        2
      )
      val defaultController4 =
        Controller(defaultState4, (controllState.standard, ""), 1)
      defaultController4.playCard should ===(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 5, List(2, 9, 0), true)
          ),
          0
        )
      )
    }

    "return correct state for undoStep and redoStep" in {
      var defaultController = new Controller(
        GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ),
        (controllState.standard, ""),
        1
      )
      defaultController.undoStep should ===(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ))

      defaultController.makeTurn
      defaultController.state = GameState(
          1,
          List(2, 3, 5, 1),
          List(
            Player("Gustav", 1, List(5, 0), true),
            Player("Guschtav", 1, List(7, 0), true)
          ),
          1
        )
      defaultController.undoStep should ===(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(0), true)
          ),
          1
        ))

      defaultController.redoStep should ===(GameState(
          1,
          List(2, 3, 5, 1),
          List(
            Player("Gustav", 1, List(5, 0), true),
            Player("Guschtav", 1, List(7, 0), true)
          ),
          1
        ))
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
        (controllState.standard, ""),
        -2
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
        (controllState.standard, ""),
        -2
      )
      defaultController2.checkUponWin should ===(false)
    }
  }
  "State Handler should return correct string" in {
    var defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.standard, ""),
      -2
    )
    defaultController.StateHandler.handle should ===(
      defaultController.StateHandler.getBoard
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.selectEffect, ""),
      -2
    )
    defaultController.StateHandler.handle should ===(
      defaultController.StateHandler.selectEffect
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.tellEliminatedPlayer, "Test"),
      -2
    )
    defaultController.StateHandler.handle should ===(
      "Spieler Test wurde eliminiert"
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.playerWins, "Test"),
      -2
    )
    defaultController.StateHandler.handle should ===(
      "Spieler Test hat die Runde gewonnen"
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.getEffectedPlayer, ""), -2
    )
    defaultController.StateHandler.handle should ===(
      "Waehle einen Spieler auf den du deine Aktion anwenden willst Vector(1)"
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.getInvestigatorGuess, ""), -2
    )
    defaultController.StateHandler.handle should ===(
      "Welchen Wert der Handkarte raetst du (0|2-8)"
    )

    defaultController = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 1, List(0), false)
        ),
        1
      ),
      (controllState.informOverPlayedEffect, "test"), -2
    )
    defaultController.StateHandler.handle should ===(
      "test"
    )

  }
  "should return the corrct Int for playCard selection" in {
    // player mad and card 15
    var contr = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 15, List(9, 0), true)
        ),
        1
      ),
      (controllState.standard, ""),
      1
    )
    contr.checkForCard7or15(2) should ===(2)

    // player not mad and handcard 15 bycard >5
    contr = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 15, List(0), true)
        ),
        6
      ),
      (controllState.standard, ""),
      1
    )
    contr.checkForCard7or15(1) should ===(2)

    // player not mad and handcard 15 bycard <5
    contr = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 15, List(0), true)
        ),
        3
      ),
      (controllState.standard, ""),
      1
    )
    contr.checkForCard7or15(1) should ===(1)

    // player not mad and Othercard 15 bycard >5
    contr = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 6, List(0), true)
        ),
        15
      ),
      (controllState.standard, ""),
      1
    )
    contr.checkForCard7or15(2) should ===(1)

    // player not mad and Othercard 15 bycard <5
    contr = Controller(
      GameState(
        1,
        List(2, 3, 4, 5, 1),
        List(
          Player("Gustav", 1, List(0), true),
          Player("Guschtav", 3, List(0), true)
        ),
        15
      ),
      (controllState.standard, ""),
      1
    )
    contr.checkForCard7or15(2) should ===(2)
  }

}
