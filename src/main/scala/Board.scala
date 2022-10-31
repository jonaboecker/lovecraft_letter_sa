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
      "|" + cards.cards.fromOrdinal(indices(0)).getValue + " " + cards
        .getTitelSnippet(
          indices(0),
          1,
          cardWith - 4
        ) + " " + cards.cards.fromOrdinal(indices(0)).getAmount + "|   " +
        "|" + cards.cards.fromOrdinal(indices(1)).getValue + " " + cards
          .getTitelSnippet(
            indices(1),
            1,
            cardWith - 4
          ) + " " + cards.cards.fromOrdinal(indices(1)).getAmount + "|   " +
        "|" + cards.cards.fromOrdinal(indices(2)).getValue + " " + cards
          .getTitelSnippet(
            indices(2),
            1,
            cardWith - 4
          ) + " " + cards.cards.fromOrdinal(indices(2)).getAmount + "|   "
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
