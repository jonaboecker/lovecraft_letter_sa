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
        val tEffect = (" ") * (cardWith - 2) + effect + (" ") * ((cardWith - 2) - effect.length % (cardWith - 2) + (cardWith - 2))
        if (mad_effect.equals("")) {
            val textLength = tEffect.length
            val bothEffects = tEffect + (" ")*((cardWith - 2) * cardHeight - textLength)
            val tempVec = splitname(bothEffects, 2)
            tempVec.map("| " + _ + " |" + eol).mkString
        } else {
            val textLength = (tEffect + "mad: " + mad_effect).length
            val bothEffects = tEffect + "mad: " + mad_effect + (" ")*((cardWith - 2) * cardHeight - textLength)
            val tempVec = splitname(bothEffects, 2)
            tempVec.map("| " + _ + " |" + eol).mkString
        }
    }

    def card = edge + title + body + edge

    override def toString: String = card
}
