package de.htwg.lovecraftletter.model

trait BoardInterface {

  val eol: String
  val cardWith: Int
  val cardHeight: Int
  val cards: Card

  def fillspace(name: String, margin: Int): String

  def edge: String

  def header(head: Int): String

  def body: String

  def title: String

  def bodybuilder(index: Vector[Int], row: Int): String

  def toString: String
}
