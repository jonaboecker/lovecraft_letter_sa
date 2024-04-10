package de.htwg.lovecraftletter.controller

import de.htwg.lovecraftletter.model.{GameStateInterface, PlayerInterface}

trait InitializerInterface {

  def playerAmount(input: Int): Unit

  def playerName(input: String): Unit

  def initialize(): GameStateInterface
}
