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
    case Investigator extends cards("1", "5", "Erraetst du den Wert der Handkarte eines Mitspielers (außer der „1\"), scheidet dieser aus.", "")
    case Katzen_von_Ulthar extends cards("2", "2", "Schaue dir die Handkarte eines Mitspielers an.", "")
    case Grosse_Rasse_von_Yith extends cards("3", "2", "Vergleiche deine Handkarte mit der eines Mitspielers. Der Spieler mit dem niedrigeren Wert scheidet aus.", "")
    case Aelteres_Zeichen extends cards("4", "2", "Du bist bis zu deinem nächsten Zug geschützt.", "")
    case Dr_Henry_Armintage extends cards("5", "2", "Waehle einen Spieler, der seine Handkarte ablegt und eine neue Karte zieht.", "")
    case Randolph_Carter extends cards("6", "1", "Tausche deine Handkarte mit der eines Mitspielers.", "")
    case Der_Silberne_Schluessel extends cards("7", "1", "Wenn du zusaetzlich eine Karte mit einer „5\" oder höher auf der Hand hast, musst du den silbernen Schlüssel ausspielen.", "")
    case Necronomicon extends cards("8", "1", "Wenn du das Necronomicon ablegst, scheidest du aus.", "")
    case Tiefe_Wesen extends cards("1", "1", "Erraetst du den Wert der Handkarte eines Mitspielers (außer der „1\"), scheidet dieser aus.", "Wähle einen Mitspieler. Besitzt dessen Handkarte eine „1\", scheidet er aus. Wenn nicht, wende die normale Funktion dieser Karte gegen den Mitspieler an.")
    case Weltraum_Met extends cards("1", "5", "Schaue dir die Handkarte eines Mitspielers an.", "Schaue dir die Handkarte eines Mitspielers an. Ziehe 1 Karte und spiele dann 1 Handkarte aus.")
    case C11 extends cards("1", "5", "Rate", "")
    case C12 extends cards("1", "5", "Rate", "")
    case C13 extends cards("1", "5", "Rate", "")
    case C14 extends cards("1", "5", "Rate", "")
    case C15 extends cards("1", "5", "Rate", "")
    case C16 extends cards("1", "5", "Rate", "")


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
