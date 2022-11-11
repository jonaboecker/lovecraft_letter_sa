class Board(val cards: Vector[Card]) {
  val eol = sys.props("line.separator")
  val v1: Vector[String] = Vector()

  def splitAtEol(card: Card): Vector[String] = card.toString.split(eol).toVector
  def append(s: String): Vector[String] = v1.appended(s)
  def connect(
      a1: Vector[String],
      a2: Vector[String],
      a3: Vector[String]
  ): Vector[String] = {
    for (i <- a1.indices) {
      a1.updated(
        i,
        (a1(i) + "   " + a2(i) + "                   " + a3(i) + eol)
      )
    }
    a1
  }
  def board = {
    val tempVec =
      connect(splitAtEol(cards(0)), splitAtEol(cards(1)), splitAtEol(cards(2)))
    " " * 17 + "Handkarten" + " " * 39 + "Ablagestapel" + eol + tempVec.mkString
  }
  override def toString: String = board
}

//def body (cards: Vector[Int]) = {
//    foreach (cards.length) {
//        add()
//    }
//
//}

class Card(
    val name: String,
    val value: Int,
    val number: Int,
    val effect: String,
    val mad_effect: String
) {
  val eol = sys.props("line.separator")
  val cardWith = 18
  val cardHeight = 12

  def fillspace(name: String, margin: Int): String =
    name + " " * (cardWith - margin - name.length)
  def splitname(name: String, margin: Int): Vector[String] = {
    if (name.length < cardWith - margin) {
      Vector(fillspace(name, margin))
    } else if (name.length > cardWith - margin) {
      val tempVec = name.grouped(cardWith - margin).toVector
      tempVec.updated(
        tempVec.length - 1,
        fillspace(tempVec(tempVec.length - 1), margin)
      )
    } else {
      Vector(name)
    }
  }

  def edge = "+" + "-" * cardWith + "+" + eol
  def title = {
    val tempVec = splitname(name, 4)
    val tempSt = "|" + value + " " + tempVec(0) + " " + number + "|" + eol
    if (tempVec.length == 1) {
      tempSt + "|" + (" ") * cardWith + "|" + eol
    } else {
      tempSt + "|  " + tempVec(1) + "  |" + eol
    }
  }
  def body = {
    val tEffect =
      (" ") * (cardWith - 2) + effect + (" ") * ((cardWith - 2) - effect.length % (cardWith - 2) + (cardWith - 2))
    if (mad_effect.equals("")) {
      val textLength = tEffect.length
      val bothEffects =
        tEffect + (" ") * ((cardWith - 2) * cardHeight - textLength)
      val tempVec = splitname(bothEffects, 2)
      tempVec.map("| " + _ + " |" + eol).mkString
    } else {
      val textLength = (tEffect + "mad: " + mad_effect).length
      val bothEffects =
        tEffect + "mad: " + mad_effect + (" ") * ((cardWith - 2) * cardHeight - textLength)
      val tempVec = splitname(bothEffects, 2)
      tempVec.map("| " + _ + " |" + eol).mkString
    }
  }

  def card = edge + title + body + edge

  override def toString: String = card
}
