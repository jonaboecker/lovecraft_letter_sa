package de.htwg.lovecraftletter.model


abstract class Board(val indices: Vector[Int], val head: Int) extends BoardInterface {
    override val eol = sys.props("line.separator")
    override val cardWith = 24
    override val cardHeight = 13
    override val cards = new Card

    override def fillspace(name: String, margin: Int): String =
        name + " " * (cardWith - margin - name.length)
    override def edge = ("+" + "-" * cardWith + "+   ") * indices.length + eol
    override def header(head: Int) = {
        head match
            case 0 => ""
            case 1 =>"         Karte 1                      Karte 2                    Ablagestapel          " + eol
    }
    override def body = {
        val res = for (x <- 1 until (cardHeight + 1)) yield bodybuilder(indices, x)
        res.mkString
    }
    override def toString: String = header(head) + edge + title + body + edge
}

object Board {
    private class OneCardBoard(override val indices: Vector[Int], override val head: Int) extends Board(indices, head){
        override def title = {
            // val tempVec = splitname(name, 4)
            val tempSt =
                "|" + cards.cards.fromOrdinal(indices(0)).getValue + " " + cards
                  .getTitelSnippet(
                      indices(0),
                      1,
                      cardWith - 4
                  ) + " " + cards.cards.fromOrdinal(indices(0)).getAmount + "|   "
                  + eol + "|  " + cards
                  .getTitelSnippet(
                      indices(0),
                      2,
                      cardWith - 4
                  ) + "  |   " + eol
            tempSt + "|" + "-" * (cardWith) + "|   " + eol
        }
        override def bodybuilder(index: Vector[Int], row: Int) = {
            "| " + cards.getEffectSnippet(index(0), row, cardWith - 2) + " |   " + eol
        }
    }

    private class ThreeCardBoard(override val indices: Vector[Int], override val head: Int) extends Board(indices, head){
        override def title = {
            // val tempVec = splitname(name, 4)
            val tempSt =
                "|" + cards.cards.fromOrdinal(indices(0)).getValue + " " + cards
                  .getTitelSnippet(
                      indices(0),
                      1,
                      cardWith - 4
                  ) + " " + cards.cards.fromOrdinal(indices(0)).getAmount + "|   " +
                  "|" + cards.cards.fromOrdinal(indices(1)).getValue + " " + cards
                  .getTitelSnippet(
                      indices(1),
                      1,
                      cardWith - 4
                  ) + " " + cards.cards.fromOrdinal(indices(1)).getAmount + "|   " +
                  "|" + cards.cards.fromOrdinal(indices(2)).getValue + " " + cards
                  .getTitelSnippet(
                      indices(2),
                      1,
                      cardWith - 4
                  ) + " " + cards.cards.fromOrdinal(indices(2)).getAmount + "|   "
                  + eol + "|  " + cards
                  .getTitelSnippet(
                      indices(0),
                      2,
                      cardWith - 4
                  ) + "  |   " + "|  " + cards
                  .getTitelSnippet(
                      indices(1),
                      2,
                      cardWith - 4
                  ) + "  |   " + "|  " + cards
                  .getTitelSnippet(
                      indices(2),
                      2,
                      cardWith - 4
                  ) + "  |   " + eol
            tempSt + "|" + "-" * (cardWith) + "|   " + "|" + "-" * (cardWith) + "|   " + "|" + "-" * (cardWith) + "|   " + eol
        }
        override def bodybuilder(index: Vector[Int], row: Int) = {
            "| " + cards.getEffectSnippet(index(0), row, cardWith - 2) + " |   " +
              "| " + cards.getEffectSnippet(index(1), row, cardWith - 2) + " |   " +
              "| " + cards.getEffectSnippet(index(2), row, cardWith - 2) + " |" + eol
        }
    }


    def apply(cardAmmount:Int, indices: Vector[Int], head: Int):Board = {
        cardAmmount match {
            case 1 => new OneCardBoard(indices, head)
            //case 2 => new TwoCardBoard(indices, head)
            case 3 => new ThreeCardBoard(indices, head)
        }
    }
}