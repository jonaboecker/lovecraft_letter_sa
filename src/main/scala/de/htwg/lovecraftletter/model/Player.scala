package scala

case class Player(name: String, hand:Int, discardPile:List[Int]) {
    def updateCardAndDiscardPile(card: Int, newDiscardPile: List[Int]): Player = {
        copy(hand = card, discardPile =  newDiscardPile)
    }
}