package de.htwg.lovecraftletter.controller.initializer

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import de.htwg.lovecraftletter.controller.{ControllerRequestActor, InitializerInterface, controllState}
import de.htwg.lovecraftletter.model.DrawPileImpl.DrawPile
import de.htwg.lovecraftletter.model.FileIO.FileIOImpl.FileIOJSON
import de.htwg.lovecraftletter.model.{DrawPileInterface, GameStateInterface}
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}


case class Initializer () extends InitializerInterface {

  val drawPile: Option[DrawPileInterface] = Some(DrawPile())
  val contr: ControllerRequestActor = ControllerRequestActor()
  var state: GameStateInterface = _


  implicit val system: ActorSystem = ActorSystem("ActorSystemInitializer")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val fileIO: FileIOJSON = FileIOJSON()

  private val route: Route = {
    path("playerAmount") {
      post {
        entity(as[String]) { input =>
          val json = Json.parse(input)
          val gs = (json \ "state").as[String]
          val player = (json \ "player").as[Int]
          state = fileIO.jsonToGameState(gs)
          playerAmount(player)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Player setydkjf"))
        }
      }
    } ~
    path("playerName") {
      post {
        entity(as[String]) { input =>
          val json = Json.parse(input)
          val gs = (json \ "state").as[String]
          val player = (json \ "player").as[String]
          state = fileIO.jsonToGameState(gs)
          playerName(player)
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Player named"))
        }
      }
    } ~
    path("initialize") {
      post {
        entity(as[String]) { input =>
          state = fileIO.jsonToGameState(input)
          initialize()
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Game initialized"))
        }
      }
    }
  }

  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("0.0.0.0", 8082).bind(route)


  private val drawPileO = drawPile match {
    case Some(b) => b
    case None => new DrawPile
  }
  override def playerAmount(input: Int): Unit = {
    contr.setVarState(state.updateCurrentPlayer(input))
    contr.setVarControllerState(controllState.initGetPlayerName, "")
    contr.notifyObservers()
  }

  override def playerName(input: String): Unit = {
    state = state.addPlayer(input)
    if (state.player.length >= state.currentPlayer) {
      state = state.updatePlayer(state.player.reverse)
      initialize()
    } else {
      contr.setVarState(state)
      contr.setVarControllerState(controllState.initGetPlayerName, "")
      contr.notifyObservers()
    }
  }

  override def initialize(): GameStateInterface = {
    state = state.updateCurrentPlayer(0)
    state = state.updateDrawPile(drawPileO.newPile)
    for (i <- state.player.indices) {
      val (newDrawPile: List[Int], hand: Int) =
        drawPileO.drawAndGet(state.drawPile)
      state = state.updateDrawPile(newDrawPile)
      val player = state.player(i).updateCardAndDiscardPile(hand, List(0))
      state = state.updatePlayer(state.player.updated(i, player))
    }
    contr.setVarState(state)
    contr.drawCard()
    contr.resetControllerState()
    contr.notifyObservers()
    state
  }

}
