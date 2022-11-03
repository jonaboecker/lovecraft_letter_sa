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
    case C9 extends cards("1", "1", "Rate", "")
    case C10 extends cards("2", "1", "Rate", "")
    case Hund_von_Tindalos
        extends cards(
          "3",
          "1",
          "Vergleiche deine Handkarte mit der eines Mitspielers. Der Spieler mit dem niedrigeren Wert scheidet aus.",
          "Wähle einen Mitspieler, der nicht wahnsinnig ist. Er scheidet aus."
        )
    case Liber_Ivonis
        extends cards(
          "4",
          "1",
          "Du bist bis zu deinem nächsten Zug geschützt.",
          "Du kannst bis zum Ende der Runde nicht ausscheiden."
        )
    case MI_GO
        extends cards(
          "5",
          "1",
          "Wähle einen Spieler, der seine Handkarte ablegt und eine neue Karte zieht.",
          "Wähle einen Mitspieler. Nimm dessen Handkarte auf deine Hand und spiele dann 1 Handkarte aus. Der Mitspieler nimmt sich den Gehirnzylinder der Mi-Go."
        )
    case Nyarlathotep
        extends cards(
          "6",
          "1",
          "Tausche deine Handkarte mit der eines Mitspielers.",
          "Nimm alle Handkarten der Mitspieler. Schave sie an und gib jedem 1 Karte deiner Wahl zurück."
        )
    case Der_leuchtende_Trapezoeder
        extends cards(
          "7",
          "1",
          "Wenn du zusätzlich eine Karte mit einer \"5\" oder höher auf der Hand hast, musst du den leuchtenden Trapezoeder ausspielen. Ignoriere dies, wenn du wahnsinnig bist.",
          "Wenn du zusätzlich eine Karte mit einer \"5\" oder höher auf der Hand hast, gewinnst du die Runde."
        )
    case Cthulhu
        extends cards(
          "8",
          "1",
          "Wenn du Cthulhu ablegst, scheidest du aus.",
          "Wenn du Cthulhu ablegst und bereits 2 oder mehr Wahnsinnskarten in deinem Ablagestapel besitzt, gewinnst du das Spiel. Wenn nicht, scheidest du aus."
        )
    case Gehirnzylinder_der_MI_GO
        extends cards(
          "0",
          "1",
          "Wenn du den Gehirnzylinder ablegst, scheidest du aus.",
          "Wenn du den Gehirnzylinder ablegst, scheidest du aus."
        )

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
