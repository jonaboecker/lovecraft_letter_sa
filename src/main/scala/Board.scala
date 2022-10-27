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
      ) + " " + cards.getAmount(indices(0)) + "|" + eol + "|  " + cards
        .getTitelSnippet(
          indices(0),
          2,
          cardWith - 4
        ) + "  |" + eol
  }
//   def body = {
//     val tEffect =
//       (" ") * (cardWith - 2) + effect + (" ") * ((cardWith - 2) - effect.length % (cardWith - 2) + (cardWith - 2))
//     if (mad_effect.equals("")) {
//       val textLength = tEffect.length
//       val bothEffects =
//         tEffect + (" ") * ((cardWith - 2) * cardHeight - textLength)
//       val tempVec = splitname(bothEffects, 2)
//       tempVec.map("| " + _ + " |" + eol).mkString
//     } else {
//       val textLength = (tEffect + "mad: " + mad_effect).length
//       val bothEffects =
//         tEffect + "mad: " + mad_effect + (" ") * ((cardWith - 2) * cardHeight - textLength)
//       val tempVec = splitname(bothEffects, 2)
//       tempVec.map("| " + _ + " |" + eol).mkString
//     }
//   }

  override def toString: String = edge + title + edge
}
