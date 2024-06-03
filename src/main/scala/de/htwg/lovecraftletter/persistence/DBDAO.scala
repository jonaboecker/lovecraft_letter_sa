package de.htwg.lovecraftletter.persistence

import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import de.htwg.lovecraftletter.model.GameStateInterface

trait DBDAO extends FileIOInterface{
  def dropDatabase(): Unit
  def createTables(): Unit
  override def save(game: GameStateInterface): Unit
  override def load(oldGameState: GameStateInterface): GameStateInterface
  def closeDB(): Unit
}
