package de.htwg.lovecraftletter.model
package FileIO

trait FileIOInterface {
  def load(game: GameStateInterface): GameStateInterface

  def save(game: GameStateInterface): Unit
}
