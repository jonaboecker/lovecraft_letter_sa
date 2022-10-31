package scala

case class Card() {
  val eol = sys.props("line.separator")
  // val cardWith = 18
  // val cardHeight = 12

  enum cards(
      value: String,
      amount: String,
      effect: String,
      mad_effect: String
  ) {

    case Blank extends cards("-", "-", "", "")
    case Investigator extends cards("1", "5", "Rate", "")

    def getValue = value

    def getAmount = amount

    def getEffect = effect

    def getMadEffect = mad_effect

  }

  def getTitelSnippet(index: Int, row: Int, width: Int) = {
    val tempString = cards.fromOrdinal(index).toString()
    fillspace(tempString.slice((row - 1) * width, row * width), width)
  }

  def getEffectSnippet(index: Int, row: Int, width: Int) = {
    val tempString =
      cards.fromOrdinal(index).getEffect + " Mad: " + cards
        .fromOrdinal(index)
        .getMadEffect
    fillspace(tempString.slice((row - 1) * width, row * width), width)
  }

  def fillspace(name: String, width: Int): String =
    name + " " * (width - name.length)
}
