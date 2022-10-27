package scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class BoardSpec extends AnyWordSpec with Matchers {

  val eol = sys.props("line.separator")

  "A Board" should {
    // val defaultBoard = new Board(1, 2, 3)

    "return the correct String" in {
      val defaultBoard = new Board(Vector(1, 1, 1))
      val EdgeResult =
        ("+------------------+   +------------------+   +------------------+" + eol)
      defaultBoard.edge() should ===(EdgeResult)
      val TitelResult = (
        "|1 Investigator   5|   |1 Investigator   5|   |1 Investigator   5|" + eol +
          "|                  |   |                  |   |                  |" + eol
      )
      defaultBoard.title() should ===(TitelResult)
      val BodyResult = (
        "| Rate             |   | Rate             |   | Rate             |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol +
          "|                  |   |                  |   |                  |" + eol
      )
      defaultBoard.body() should ===(BodyResult)

      defaultBoard.toString() should ===(
        EdgeResult + TitelResult + BodyResult + EdgeResult
      )
    }

  }
}
