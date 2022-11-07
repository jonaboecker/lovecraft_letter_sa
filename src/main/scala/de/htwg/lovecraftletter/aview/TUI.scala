package de.htwg.lovecraftletter
package aview

import controller._
import util.Observer

import scala.io.StdIn.readLine


final case class TUI(controller:Controller) extends Observer {
    controller.add(this)

    def runLL = {
        val playerList: List[Player] = createPlayers
        controller.initialize(playerList)
        show("Viel Spass beim spielen")
        //controller.getInputAndPrintLoop()
    }

    def createPlayers:List[Player] = {
        show("Bitte Spieleranzahl zwischen 3 und 6 angeben")
        val playerList: List[Player] = rekCreatePlayers(Nil, controller.playerAmount(readLine))
        playerList
    }

    def rekCreatePlayers(playerList: List[Player], playerAmount: Int): List[Player] = {
        if(playerAmount == 0) {
            createPlayers
        } else {
            printf("Bitte Namen fuer Spieler %d angeben\n", playerList.length + 1)
            val input = readLine
            val updatedList = Player(input, 0, Nil) :: playerList
            if (updatedList.length != playerAmount) {
                rekCreatePlayers(updatedList, playerAmount)
            } else {
                updatedList.reverse
            }
        }
    }

    def show(s:String):Unit = {
        println(s)
    }

    override def update = show(controller.getBoard)
}


