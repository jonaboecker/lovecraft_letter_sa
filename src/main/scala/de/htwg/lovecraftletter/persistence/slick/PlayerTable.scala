package de.htwg.lovecraftletter.persistence.slick

import slick.jdbc.PostgresProfile.api.*
import slick.lifted.ProvenShape

case class PlayerRow(name: String, hand: Int, discardPile: String, inGame: Boolean, id: Int)

class PlayerTable(tag: Tag) extends Table[PlayerRow](tag, "player") {
  def id = column[Int]("id", O.PrimaryKey)
  
  private def name = column[String]("name")

  private def hand = column[Int]("hand")

  private def discardPile = column[String]("discardPile") // Speichern Sie die Liste als String

  private def inGame = column[Boolean]("inGame")

  override def * : ProvenShape[PlayerRow] = (name, hand, discardPile, inGame, id) <> (PlayerRow.tupled, PlayerRow.unapply)
}

object PlayerRow {
  def tupled: ((String, Int, String, Boolean, Int)) => PlayerRow = (PlayerRow.apply _).tupled
}
