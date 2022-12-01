package de.htwg.lovecraftletter.util

trait Command[T]:
  def doStep(t: T): T
  def undoStep(t: T): T
  def redoStep(t: T): T

class UndoManager[T]:
  private var undoStack: List[T] = Nil
  private var redoStack: List[T] = Nil
  def doStep(t: T, command: Command[T]) =
    undoStack = t :: undoStack
    command.doStep(t)
  def undoStep(t: T): T =
    undoStack match {
      case Nil => t
      case head :: stack => {
        //val result = head.undoStep(t)
        undoStack = stack
        redoStack = t :: redoStack
        head
      }
    }
  def redoStep(t: T): T =
    redoStack match {
      case Nil => t
      case head :: stack => {
        //val result = head.redoStep(t)
        redoStack = stack
        undoStack = t :: undoStack
        head
      }
    }
