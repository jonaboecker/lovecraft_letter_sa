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
  }
}
