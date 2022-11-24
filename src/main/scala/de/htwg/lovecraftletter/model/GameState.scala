package de.htwg.lovecraftletter.model

final case class GameState(
    var currentPlayer: Int,
    var drawPile: List[Int],
    var player: List[Player],
    var currentCard: Int
) {

  def nextPlayer: GameState = {
    GameState(
      (currentPlayer + 1) % player.length,
      drawPile,
      player,
      currentCard
    )
  }

  def drawCard: GameState = {
    val drawPileObject = new DrawPile
    val (tempDrawPile: List[Int], tempCard: Int) =
      drawPileObject.drawAndGet(drawPile)
    GameState(currentPlayer, tempDrawPile, player, tempCard)
  }

  def playCard: GameState = {
    val tempPlayer = player.updated(
      currentPlayer,
      player(currentPlayer).discardCard(currentCard)
    )
    GameState(currentPlayer, drawPile, tempPlayer, 0)
  }

  def swapHandAndCurrent: GameState = {
    val tempPlayer = player.updated(
      currentPlayer,
      player(currentPlayer).changeHand(currentCard)
    )
    GameState(currentPlayer, drawPile, tempPlayer, player(currentPlayer).hand)
  }

  def eliminatePlayer(toEliminatePlayer: Int): GameState = {
    val tempPlayer =
      player.updated(
        toEliminatePlayer,
        player(toEliminatePlayer).eliminatePlayer()
      )
    GameState(currentPlayer, drawPile, tempPlayer, currentCard)
  }
}
