//val eol = sys.props("line.separator")
//def bar() = (("+" + "-" * 5) * 5) + "+" + eol

@main def hello: Unit =
    println("Hello world!!")
    println(msg)
    // println(bar())
    val card1 = new Card("Investigator", 1, 5, "Rate", "")
    val card2 = new Card(
        "Gehirnzylinder der MI-GO",
        0,
        1,
        "Wenn du den Gehirnzylinder ablegst, scheidest du aus",
        "Wenn du den Gehirnzylinder ablegst, scheidest du aus"
    )
    val card3 = new Card("Der leuchtende Trapezoeder", 7, 1, "Wenn du zusätzlich eine Karte mit einer \"5\" oder höher auf der Hand hast, musst du den leuchtenden Trapezoeder ausspielen. Ignoriere dies, wenn du wahnsinnig bist.", "Wenn du zusätzlich eine Karte mit einer \"5\" oder höher auf der Hand hast, gewinnst du die Runde.")
    // println(card)
    // println(card2)
    var tempVec = Vector(card1, card2, card3)
    print(new Board(tempVec))

def msg = "I was compiled by Scala 3. :)"
