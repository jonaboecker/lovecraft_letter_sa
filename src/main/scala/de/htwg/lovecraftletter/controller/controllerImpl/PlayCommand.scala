package de.htwg.lovecraftletter
package controller

import util._
import model.GameStateInterface
import de.htwg.lovecraftletter.model.GameStateInterface

class PlayCommand(contr: ControllerInterface) extends Command[GameStateInterface]{
    def doStep(state: GameStateInterface): GameStateInterface = contr.playCard
    def undoStep(state: GameStateInterface): GameStateInterface = contr.undoStep
    def redoStep(state: GameStateInterface): GameStateInterface = contr.redoStep
}
