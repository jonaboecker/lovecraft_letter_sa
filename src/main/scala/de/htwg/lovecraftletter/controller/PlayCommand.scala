package de.htwg.lovecraftletter
package controller

import util._
import model.GameState

class PlayCommand(contr: Controller) extends Command[GameState]{
    def doStep(state: GameState): GameState = contr.playCard
    def undoStep(state: GameState): GameState = contr.undoStep
    def redoStep(state: GameState): GameState = contr.redoStep
}
