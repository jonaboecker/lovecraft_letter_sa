class Card (val name: String, val value: Int, val number: Int, val effect: String, val mad_effect: String){
    val eol = sys.props("line.separator")
    val cardWith = 18
    val cardHeight = 12

    def fillspace(name: String, margin: Int): String = name + " " * (cardWith -margin - name.length)
    def splitname (name: String, margin: Int): Vector[String] = {
        if (name.length < cardWith -margin) {
            Vector(fillspace(name, margin))
        } else if (name.length > cardWith -margin) {
            val tempVec = name.grouped(cardWith -margin).toVector
            tempVec.updated(tempVec.length - 1, fillspace(tempVec(tempVec.length - 1), margin))
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
        //("|" + ("H") * cardWith + "|" + eol) * cardHeight
        val tempVec = splitname(effect, 2)
        //val tempSt = "Hallo"
        val tempArray: Array[String] = tempVec.toArray
        for (i <- 0 until cardHeight - 1) {
            if (tempVec.length > i) {
                tempArray.updated(0, tempArray(0) + "|" + tempVec(i) + "|" + eol)
                //tempSt.appended("|" + tempVec(i) + "|" + eol)
            } else {
                //tempSt.appended("|" + ("H") * cardWith + "|" + eol)
                tempArray.updated(0, tempArray(0) + "|" + ("H") * cardWith + "|" + eol)
            }
        }
        //tempSt
        tempArray(0)
    }

    def card = edge + title + body + edge

    override def toString: String = card
}
