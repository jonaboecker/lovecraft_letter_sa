package de.htwg.lovecraftletter.persistence.slick

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.ProvenShape

case class GameStateRow(currentPlayer: Int, drawPile: String, playerAmount:Int, player1: Int, player2: Int, player3: Int, player4: Int, player5: Int, player6: Int, currentCard: Int, id: Int = 0)

class GameStateTable(tag: Tag) extends Table[GameStateRow](tag, "gameState") {
  private def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  private def currentPlayer = column[Int]("currentPlayer")

  private def drawPile = column[String]("drawPile") // Speichern Sie die Liste als String
  
  private def playerAmount = column[Int]("playerAmount")

  private def player1 = column[Int]("player1")

  private def player2 = column[Int]("player2")

  private def player3 = column[Int]("player3")

  private def player4 = column[Int]("player4")

  private def player5 = column[Int]("player5")

  private def player6 = column[Int]("player6")

  private def currentCard = column[Int]("currentCard")

  def * : ProvenShape[GameStateRow] = (currentPlayer, drawPile, playerAmount, player1, player2, player3, player4, player5, player6, currentCard, id) <> (GameStateRow.tupled, GameStateRow.unapply)
}

object GameStateRow {
  def tupled: ((Int, String, Int, Int, Int, Int, Int, Int, Int, Int, Int)) => GameStateRow = (GameStateRow.apply _).tupled
}
