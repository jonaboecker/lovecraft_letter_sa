package de.htwg.lovecraftletter
package aview

import controller._
import util.Observer

import scala.io.StdIn.readLine


final case class TUI(controller:Controller) extends Observer {
    controller.add(this)

    override def update(state:Int) = {
        state match
            case 0 =>
                show("Bitte Spieleranzahl zwischen 3 und 6 angeben.")
                readLine
        
    }

    def run = {
        controller.initialize
        controller.getInputAndPrintLoop
    }

    def show(s:String):Unit = {
        println(s)
    }


}


