package scala

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest._

class BoardSpec extends AnyWordSpec with Matchers {

  val eol = sys.props("line.separator")

  "A Board" should {
    val defaultBoard = new Board(Vector(0, 0, 0),0)

    "return the correct String for fillspace" in {
      defaultBoard.fillspace("Hallo", 10) should ===("Hallo         ")
    }

    "return the correct EdgeString" in {
      val EdgeResult =
        ("+------------------------+   +------------------------+   +------------------------+   " + eol)
      defaultBoard.edge should ===(EdgeResult)
    }

    "return the correct TitleString" in {
      val TitelResult = (
        "|- Blank                -|   |- Blank                -|   |- Blank                -|   " + eol +
          "|                        |   |                        |   |                        |   " + eol +
          "|------------------------|   |------------------------|   |------------------------|   " + eol
      )
      defaultBoard.title should ===(TitelResult)
    }

    "return the correct BodyString" in {
      val BodyResult = (
        "| Mad:                   |   | Mad:                   |   | Mad:                   |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol +
          "|                        |   |                        |   |                        |" + eol
      )
      defaultBoard.body should ===(BodyResult)
    }

    "return the correct String form bodybuilder" in {
      defaultBoard.bodybuilder(Vector(0, 0, 0), 1) should ===(
        "| Mad:                   |   | Mad:                   |   | Mad:                   |" + eol
      )
    }

    "return the correct String for toString" in {
      val result =
        defaultBoard.edge + defaultBoard.title + defaultBoard.body + defaultBoard.edge
      defaultBoard.toString should ===(result)
    }
    "return the correct String from header" in {
        defaultBoard.header(0) should === ("")
        defaultBoard.header(1) should === ("         Karte 1                      Karte 2                    Ablagestapel          " + eol)
    }

  }
}
