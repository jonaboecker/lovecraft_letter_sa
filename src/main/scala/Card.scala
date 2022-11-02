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
    case C2 extends cards("1", "5", "Rate", "")
    case C3 extends cards("1", "5", "Rate", "")
    case C4 extends cards("1", "5", "Rate", "")
    case C5 extends cards("1", "5", "Rate", "")
    case C6 extends cards("1", "5", "Rate", "")
    case C7 extends cards("1", "5", "Rate", "")
    case C8 extends cards("1", "5", "Rate", "")
    case C9 extends cards("1", "5", "Rate", "")
    case C10 extends cards("1", "5", "Rate", "")
    case C11 extends cards("1", "5", "Rate", "")
    case C12 extends cards("1", "5", "Rate", "")
    case C13 extends cards("1", "5", "Rate", "")
    case C14 extends cards("1", "5", "Rate", "")
    case C15 extends cards("1", "5", "Rate", "")
    case C16 extends cards("1", "5", "Rate", "")


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
