package de.htwg.lovecraftletter.model

final case class GameState(var currentPlayer:Int, var newDrawPile:List[Int], var player:List[Player]) {
    
}
