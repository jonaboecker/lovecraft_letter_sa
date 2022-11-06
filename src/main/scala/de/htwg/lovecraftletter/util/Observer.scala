package de.htwg.lovecraftletter
package util

trait Observer:
  def update(state:Int): Unit

trait Observable:
  var subscribers: Vector[Observer] = Vector()
  def add(s: Observer) = subscribers = subscribers :+ s
  def remove(s: Observer) = subscribers = subscribers.filterNot(o => o == s)
  def notifyObservers(state:Int) = subscribers.foreach(o => o.update(state))
