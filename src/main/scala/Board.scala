class Board (val cards: Vector[Card]){
    val eol = sys.props("line.separator")

    def splitAtEol (card: Card): Vector[String] = card.toString.split(eol).toVector
    //def append (s: String): Vector[String] = v1.appended(s)
    def connect (a1: Vector[String], a2: Vector[String], a3: Vector[String]): Vector[String] = {
        val a4 = a1.toArray
        for (i <- a1.indices) {
            a4(i) = a1(i) + "   " + a2(i) + "                   " + a3(i) + eol
        }
        a4.toVector
    }
    def board = {
        val tempVec = connect(splitAtEol(cards(0)), splitAtEol(cards(1)), splitAtEol(cards(2)))
        " " * 17 + "Handkarten" + " " * 39 + "Ablagestapel" + eol + tempVec.mkString
    }
    override def toString: String = board
}
