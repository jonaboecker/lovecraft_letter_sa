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
    case Investigator
        extends cards(
          "1",
          "5",
          "Erraetst du den Wert der Handkarte eines Mitspielers (außer der „1\"), scheidet dieser aus.",
          ""
        )
    case Katzen_von_Ulthar
        extends cards(
          "2",
          "2",
          "Schaue dir die Handkarte eines Mitspielers an.",
          ""
        )
    case Grosse_Rasse_von_Yith
        extends cards(
          "3",
          "2",
          "Vergleiche deine Handkarte mit der eines Mitspielers. Der Spieler mit dem niedrigeren Wert scheidet aus.",
          ""
        )
    case Aelteres_Zeichen
        extends cards(
          "4",
          "2",
          "Du bist bis zu deinem naechsten Zug geschuetzt.",
          ""
        )
    case Dr_Henry_Armintage
        extends cards(
          "5",
          "2",
          "Waehle einen Spieler, der seine Handkarte ablegt und eine neue Karte zieht.",
          ""
        )
    case Randolph_Carter
        extends cards(
          "6",
          "1",
          "Tausche deine Handkarte mit der eines Mitspielers.",
          ""
        )
    case Der_Silberne_Schluessel
        extends cards(
          "7",
          "1",
          "Wenn du zusaetzlich eine Karte mit einer „5\" oder hoeher auf der Hand hast, musst du den silbernen Schluessel ausspielen.",
          ""
        )
    case Necronomicon
        extends cards(
          "8",
          "1",
          "Wenn du das Necronomicon ablegst, scheidest du aus.",
          ""
        )
    case Tiefe_Wesen
        extends cards(
          "1",
          "1",
          "Erraetst du den Wert der Handkarte eines Mitspielers (außer der „1\"), scheidet dieser aus.",
          "Waehle einen Mitspieler. Besitzt dessen Handkarte eine „1\", scheidet er aus. Wenn nicht, wende die normale Funktion dieser Karte gegen den Mitspieler an."
        )
    case Weltraum_Met
        extends cards(
          "1",
          "5",
          "Schaue dir die Handkarte eines Mitspielers an.",
          "Schaue dir die Handkarte eines Mitspielers an. Ziehe 1 Karte und spiele dann 1 Handkarte aus."
        )
    case Hund_von_Tindalos
        extends cards(
          "3",
          "1",
          "Vergleiche deine Handkarte mit der eines Mitspielers. Der Spieler mit dem niedrigeren Wert scheidet aus.",
          "Waehle einen Mitspieler, der nicht wahnsinnig ist. Er scheidet aus."
        )
    case Liber_Ivonis
        extends cards(
          "4",
          "1",
          "Du bist bis zu deinem naechsten Zug geschuetzt.",
          "Du kannst bis zum Ende der Runde nicht ausscheiden."
        )
    case MI_GO
        extends cards(
          "5",
          "1",
          "Waehle einen Spieler, der seine Handkarte ablegt und eine neue Karte zieht.",
          "Waehle einen Mitspieler. Nimm dessen Handkarte auf deine Hand und spiele dann 1 Handkarte aus. Der Mitspieler nimmt sich den Gehirnzylinder der Mi-Go."
        )
    case Nyarlathotep
        extends cards(
          "6",
          "1",
          "Tausche deine Handkarte mit der eines Mitspielers.",
          "Nimm alle Handkarten der Mitspieler. Schave sie an und gib jedem 1 Karte deiner Wahl zurueck."
        )
    case Der_leuchtende_Trapezoeder
        extends cards(
          "7",
          "1",
          "Wenn du zusaetzlich eine Karte mit einer \"5\" oder hoeher auf der Hand hast, musst du den leuchtenden Trapezoeder ausspielen. Ignoriere dies, wenn du wahnsinnig bist.",
          "Wenn du zusaetzlich eine Karte mit einer \"5\" oder hoeher auf der Hand hast, gewinnst du die Runde."
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
