package de.htwg.lovecraftletter
package aview

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import de.htwg.lovecraftletter.controller.{ControllerInterface, controllState}
import de.htwg.lovecraftletter.util.Observer

import scala.concurrent.{ExecutionContextExecutor, Future}

class RestService(using controller: ControllerInterface) extends Observer {
  implicit val system: ActorSystem = ActorSystem("mySystem")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  controller.add(this)

  private val route: Route = {
    path("gameState") {
      get {
        complete(controller.handle)
      }
    } ~ 
    path("turn") {
      post {
        entity(as[String]) { input =>
          handle(input)
          //controller.setVarUserInput(input.toInt)
          //controller.makeTurn
          complete("Turn made")
        }
      }
    }
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8080).bind(route)

  private def handle(input: String) = {
    controller.getVarControllerState(0) match
      case controllState.standard =>
        input match
          case "1" =>
            controller.setVarUserInput(1)
            controller.makeTurn
          case "2" =>
            controller.setVarUserInput(2)
            controller.makeTurn
      case controllState.getEffectedPlayer =>
        controller.playerChosen(input.toInt)
      case controllState.getInvestigatorGuess =>
        controller.investgatorGuessed(input.toInt)
      case controllState.getInputToPlayAnotherCard =>
        controller.playAnotherCard2(input.toInt)
      case controllState.initGetPlayerAmount =>
        controller.playerAmount(input.toInt)
      case controllState.initGetPlayerName =>
        controller.playerName(input)
      case _ => controller.resetControllerState()
  }

  override def update: Unit = {
    // Update your state here
  }
}
