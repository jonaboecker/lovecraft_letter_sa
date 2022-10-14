class Card (val name: String, val value: Int, val number: Int, val effect: String, val mad_effect: String){
    val eol = sys.props("line.separator")
    val cardWith = 18
    val cardHeight = 12

    def fillspace(name: String): String = name + " " * (cardWith -4 - name.length)
    def splitname (name: String): Vector[String] = {
        if (name.length < cardWith -4) {
            Vector(fillspace(name))
        } else if (name.length > cardWith -4) {
            //val tempAr = name.split("^[A-Za-z]$",12)
            val tempVec = name.grouped(cardWith -4).toVector
            tempVec.updated(1, fillspace(tempVec(1)))
        } else {
            Vector(name)
        }
    }

    def edge = "+" + "-" * cardWith + "+" + eol
    def title = {
        val tempVec = splitname(name)
        val tempSt = "|" + value + " " + tempVec(0) + " " + number + "|" + eol
        if (tempVec.length == 1) {
            tempSt + "|" + (" ") * cardWith + "|" + eol
        } else {
            tempSt + "|  " + tempVec(1) + "  |" + eol
        }
    }
    def body = ("|" + (" ") * cardWith + "|" + eol) * cardHeight

    def card = edge + title + body + edge

  override def toString: String = card
}
