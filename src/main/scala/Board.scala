package scala

class Board(val indices: Vector[Int]) {
  val eol = sys.props("line.separator")
  val cardWith = 18
  val cardHeight = 12
  val cards = new Card

  def fillspace(name: String, margin: Int): String =
    name + " " * (cardWith - margin - name.length)

  def edge = ("+" + "-" * cardWith + "+   ") * 3 + eol

  def title = {
    // val tempVec = splitname(name, 4)
    val tempSt =
      "|" + cards.getValue(indices(0)) + " " + cards.getTitelSnippet(
        indices(0),
        1,
        cardWith - 4
      ) + " " + cards.getAmount(indices(0)) + "|   " +
        "|" + cards.getValue(indices(1)) + " " + cards.getTitelSnippet(
          indices(1),
          1,
          cardWith - 4
        ) + " " + cards.getAmount(indices(1)) + "|   " +
        "|" + cards.getValue(indices(2)) + " " + cards.getTitelSnippet(
          indices(2),
          1,
          cardWith - 4
        ) + " " + cards.getAmount(indices(2)) + "|   "
        + eol + "|  " + cards
          .getTitelSnippet(
            indices(0),
            2,
            cardWith - 4
          ) + "  |   " + "|  " + cards
          .getTitelSnippet(
            indices(1),
            2,
            cardWith - 4
          ) + "  |   " + "|  " + cards
          .getTitelSnippet(
            indices(2),
            2,
            cardWith - 4
          ) + "  |   " + eol
    tempSt
  }

  def bodybuilder(index: Vector[Int], row: Int) = {
    "| " + cards.getEffectSnippet(index(0), row, cardWith - 2) + " |   " +
      "| " + cards.getEffectSnippet(index(1), row, cardWith - 2) + " |   " +
      "| " + cards.getEffectSnippet(index(2), row, cardWith - 2) + " |" + eol
  }
  def body = {
    val res = for (x <- 1 until (cardHeight + 1)) yield bodybuilder(indices, x)
    res.mkString
  }

  override def toString: String = edge + title + body + edge
}
