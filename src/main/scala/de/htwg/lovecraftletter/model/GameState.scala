package de.htwg.lovecraftletter.model

final case class GameState(var currentPlayer:Int, var drawPile:List[Int], var player:List[Player], var currentCard:Int) {
    
}
