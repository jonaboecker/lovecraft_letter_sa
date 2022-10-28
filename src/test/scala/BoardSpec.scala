package scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class BoardSpec extends AnyWordSpec with Matchers {

  val eol = sys.props("line.separator")

  "A Board" should {
    val defaultBoard = new Board(Vector(1, 1, 1))

    "return the correct String for fillspace" in {
      defaultBoard.fillspace("Hallo", 10) should ===("Hallo   ")
    }

    "return the correct EdgeString" in {
      val EdgeResult =
        ("+------------------+   +------------------+   +------------------+   " + eol)
      defaultBoard.edge should ===(EdgeResult)
    }

    "return the correct TitleString" in {
      val TitelResult = (
        "|1 Investigator   5|   |1 Investigator   5|   |1 Investigator   5|   " + eol +
          "|                  |   |                  |   |                  |   " + eol
      )
      defaultBoard.title should ===(TitelResult)
    }

    "return the correct BodyString" in {
      val BodyResult = (
        "| Rate Mad:        |   | Rate Mad:        |   | Rate Mad:        |" + eol +
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
      defaultBoard.body should ===(BodyResult)
    }

    "return the correct String form bodybuilder" in {
      defaultBoard.bodybuilder(Vector(1, 1, 1), 1) should ===(
        "| Rate Mad:        |   | Rate Mad:        |   | Rate Mad:        |" + eol
      )
    }

    "return the correct String for toString" in {
      val result =
        defaultBoard.edge + defaultBoard.title + defaultBoard.body + defaultBoard.edge
      defaultBoard.toString should ===(result)
    }

  }
}
