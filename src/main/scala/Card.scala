package scala

class Card() {
  val eol = sys.props("line.separator")
  // val cardWith = 18
  // val cardHeight = 12

  val cards: Vector[Vector[String]] =
    Vector(
      Vector(
        "",
        "-",
        "-",
        "",
        ""
      ),
      Vector(
        "Investigator",
        "1",
        "5",
        "Rate",
        ""
      ),
      Vector(
      )
    )

  def getName(index: Int) = {
    cards(index)(0)
  }

  def getValue(index: Int) = {
    cards(index)(1)
  }

  def getAmount(index: Int) = {
    cards(index)(2)
  }

  def getEffect(index: Int) = {
    cards(index)(3)
  }

  def getMadEffect(index: Int) = {
    cards(index)(4)
  }

  def getTitelSnippet(index: Int, row: Int, width: Int) = {
    val tempString = cards(index)(0)
    fillspace(tempString.slice((row - 1) * width, row * width), width)
  }

  def getEffectSnippet(index: Int, row: Int, width: Int) = {
    val tempString = cards(index)(3) + " Mad: " + cards(index)(4)
    fillspace(tempString.slice((row - 1) * width, row * width), width)
  }

  def fillspace(name: String, width: Int): String =
    name + " " * (width - name.length)
}
