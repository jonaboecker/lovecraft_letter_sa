package de.htwg.lovecraftletter
package controller

import model._
import util.Observable


import scala.io.StdIn.readLine

case class Controller(var state:GameState) extends Observable {
  val drawPileO = new DrawPile

  def initialize(playerList: List[Player]) = {
    state.currentPlayer = 0;
    state.drawPile = drawPileO.newPile
    state.player = playerList
    for(i <- 0 until playerList.length) {
        val (newDrawPile:List[Int], hand:Int) = drawPileO.drawAndGet(state.drawPile)
        state.drawPile = newDrawPile
        val player = state.player(i).updateCardAndDiscardPile(hand, List(0))
        state.player = state.player.updated(i, player)
    }
    notifyObservers
  }

  def playerAmount(input:String): Int = {
    input match
      case "3" => 3
      case "4" => 4
      case "5" => 5
      case "6" => 6
      case _   => 0
  }

  def getBoard = {
    val currentPlayer = state.player(state.currentPlayer)
    val board = Board(Vector(state.currentCard, currentPlayer.hand, currentPlayer.discardPile.head), 1)
    board.toString
  }  

  def rekLegeAblagestapelAn(
      discardList: List[List[Int]],
      playerAmount: Int
  ): List[List[Int]] = {
    val newTile: List[Int] = List(0)
    val updatedList = newTile :: discardList
    if (updatedList.length != playerAmount) {
      rekLegeAblagestapelAn(updatedList, playerAmount)
    } else {
      updatedList
    }
  }

  

  def getInputAndPrintLoop(
      lastPlayer: Int,
      playerList: List[Player],
      drawPile: List[Int],
      hands: List[Int],
      discardPiles: List[List[Int]]
  ): Unit = {
    val currentPlayer = (lastPlayer + 1) % playerList.length
    val (newDrawPile: List[Int], drawedCard: Int) =
      drawPileO.drawAndGet(drawPile)
    printf("\n%s ist an der Reihe\n", playerList(currentPlayer))
    print(
      Board(
        Vector(
          drawedCard,
          hands(currentPlayer),
          discardPiles(currentPlayer).head
        ), 1
      )
    )
    println("Welche Karte moechtest du spielen? (1|2)")
    val input = readLine
    input match
      case "1" =>
        val newDiscardPiles = playCard(currentPlayer, drawedCard, discardPiles)
        getInputAndPrintLoop(
          currentPlayer,
          playerList,
          newDrawPile,
          hands,
          newDiscardPiles
        )
      case "2" =>
        val newDiscardPiles =
          playCard(currentPlayer, hands(currentPlayer), discardPiles)
        val newHands = hands.updated(currentPlayer, drawedCard)
        getInputAndPrintLoop(
          currentPlayer,
          playerList,
          newDrawPile,
          newHands,
          newDiscardPiles
        )
      case "q" | "Q" => return
      case _ =>
        println("1 oder 2 einzugeben ist doch wirklich nicht schwierig oder?")
        getInputAndPrintLoop(
          lastPlayer,
          playerList,
          drawPile,
          hands,
          discardPiles
        )
  }

  def playCard(
      lastPlayer: Int,
      card: Int,
      discardPiles: List[List[Int]]
  ): List[List[Int]] = {
    val newPlayerDiscardPile = card :: discardPiles(lastPlayer)
    discardPiles.updated(lastPlayer, newPlayerDiscardPile)
  }
}
