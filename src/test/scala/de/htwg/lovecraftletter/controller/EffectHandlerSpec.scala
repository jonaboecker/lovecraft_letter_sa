package de.htwg.lovecraftletter
package controller

import model._

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class EffectHandlerSpec extends AnyWordSpec with Matchers {
  "A EffectHandler" should {
    "return the correct state for play Card 5/13" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(1, 0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ))

        //Cthulhu(16) abwerfen + gewinnt
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 16, List(11, 9, 0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)
        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(16, 11, 9, 0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ))

        //Cthulhu(16) abwerfen + verliert
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 16, List(0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)
        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(16, 0), false),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ))

        //8 abwerfen + verliert
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 8, List(0), true),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)
        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(8, 0), false),
            Player("Guschtav", 1, List(5, 0), true)
          ),
          1
        ))
    }
    "return the correct state for play Card 6/14" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(6, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 1, List(6, 0), true)
          ),
          1
        ))
    }
    "return the correct state for play Card 3/11" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(3, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), false),
            Player("Guschtav", 2, List(3, 0), true)
          ),
          1
        ))
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 1, List(3, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 1, List(3, 0), false)
          ),
          1
        ))
    }
    "return the correct state for play Card 7/15" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(7, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(7, 0), true)
          ),
          1
        ))
    }
    "return the correct state for play Card 8/16/17" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(8, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 1).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(8, 0), false)
          ),
          1
        ))
    }
    "return the correct state for play Card 17 Mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(17, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(17, 0), false)
          ),
          1
        ))
    }
    "return the correct state for play Card 9 mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(9, 0), true)
          ),
          1
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), false),
            Player("Guschtav", 2, List(9, 0), true)
          ),
          1
        ))
    }
    "return the correct state for play Card 10 mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(10, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(2, 10, 0), true)
          ),
          0
        ))
    }
    "return the correct state for play Card 11 mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(11, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), false),
            Player("Guschtav", 2, List(11, 0), true)
          ),
          0
        ))
    }
    "return the correct state for play Card 13 Mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 2, List(13, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 17, List(0), true),
            Player("Guschtav", 2, List(2, 13, 0), true)
          ),
          0
        ))
    }
    "return the correct state for play Card 15 Mad" in {
        //wins
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 13, List(15, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 13, List(15, 0), true)
          ),
          0
        ))
        //dont win
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 2, List(15, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 2, List(15, 0), true)
          ),
          0
        ))
    }
    "return the correct state for play Card 16 Mad" in {
        //wins
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 13, List(16, 13, 12, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 13, List(16, 13, 12, 0), true)
          ),
          0
        ))
        //dont win
        contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 2, List(16, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        EffectHandler(contr, contr.state, 2).strategy should === (GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 2, List(0), true),
            Player("Guschtav", 2, List(16, 0), false)
          ),
          0
        ))
    }
    "return the correct state for play Card 14 Mad" in {
        //standard
        var contr = Controller(GameState(
          1,
          List(2, 3, 4, 5, 1),
          List(
            Player("Gustav", 1, List(0), true),
            Player("Guschtav", 2, List(14, 0), true),
            Player("3", 3, List(14, 0), true),
            Player("4", 4, List(14, 0), true)
          ),
          0
        ), (controllState.standard, ""), 1)

        val result = EffectHandler(contr, contr.state, 2).strategy
        result.drawPile should not be empty
    }
  }
}
