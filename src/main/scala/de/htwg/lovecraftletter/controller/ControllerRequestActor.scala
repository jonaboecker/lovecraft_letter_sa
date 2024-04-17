package de.htwg.lovecraftletter.controller

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.htwg.lovecraftletter.model.FileIO.FileIOImpl.FileIOJSON
import de.htwg.lovecraftletter.model.GameStateInterface
import play.api.libs.json.Json

import scala.concurrent.{Await, ExecutionContextExecutor, TimeoutException}
import scala.concurrent.duration.*

class ControllerRequestActor {
  implicit val system: ActorSystem = ActorSystem("ActorSystemInitializer")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val fileIO: FileIOJSON = FileIOJSON()

  def notifyObservers(): Unit = {
    val request = HttpRequest(uri = "http://localhost:8081/notifyObservers")
    val responseFuture = Http().singleRequest(request)

    try {
      val response = Await.result(responseFuture, 10.seconds)
      println("Response: " + response)
    } catch {
      case _: TimeoutException => println("Request timed out")
      case e: Exception => println("Something went wrong: " + e.getMessage)
    }
  }

  def resetControllerState(): Unit = {
    setVarControllerState(controllState.standard, "")
  }

  def drawCard(): Unit = {
    val request = HttpRequest(uri = "http://localhost:8081/drawCard")
    val responseFuture = Http().singleRequest(request)

    try {
      val response = Await.result(responseFuture, 10.seconds)
      println("Response: " + response)
    } catch {
      case _: TimeoutException => println("Request timed out")
      case e: Exception => println("Something went wrong: " + e.getMessage)
    }
  }

  def setVarState(state: GameStateInterface): Unit = {
    val stateJson = fileIO.gameStateToJSON(state)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8081/setVarGameState",
      entity = HttpEntity(ContentTypes.`application/json`, stateJson)
    )
    val responseFuture = Http().singleRequest(request)

    try {
      val response = Await.result(responseFuture, 10.seconds)
      println("Response: " + response)
    } catch {
      case _: TimeoutException => println("Request timed out")
      case e: Exception => println("Something went wrong: " + e.getMessage)
    }
  }

  def setVarControllerState(state: controllState, value: String): Unit = {
    val jsString = Json.obj(
      "cs" -> state.ordinal,
      "s" -> value
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8081/setVarControllerState",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)


    try {
      val response = Await.result(responseFuture, 10.seconds)
      println("Response: " + response)
    } catch {
      case _: TimeoutException => println("Request timed out")
      case e: Exception => println("Something went wrong: " + e.getMessage)
    }
  }

  def eliminatePlayer(player: Int): GameStateInterface = {
    val jsString = Json.obj(
      "player" -> player.toString
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8081/eliminatePlayer",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)

    val response = Await.result(responseFuture, 10.seconds)
    println("Response: " + response)

    val responseEntity = Await.result(response.entity.toStrict(2.seconds), 2.seconds)
    val responseString = Unmarshal(responseEntity).to[String].value.get.get

    println("Response string: " + responseString)

    fileIO.jsonToGameState(responseString)
  }

  def playAnotherCard(): GameStateInterface = {
    val request = HttpRequest(uri = "http://localhost:8081/playAnotherCard")
    val responseFuture = Http().singleRequest(request)

    val response = Await.result(responseFuture, 10.seconds)
    println("Response: " + response)

    val responseEntity = Await.result(response.entity.toStrict(2.seconds), 2.seconds)
    val responseString = Unmarshal(responseEntity).to[String].value.get.get

    println("Response string: " + responseString)

    fileIO.jsonToGameState(responseString)
  }

  def playerWins(player: Int): GameStateInterface = {
    val jsString = Json.obj(
      "player" -> player.toString
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://localhost:8081/playerWins",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)

    val response = Await.result(responseFuture, 10.seconds)
    println("Response: " + response)

    val responseEntity = Await.result(response.entity.toStrict(2.seconds), 2.seconds)
    val responseString = Unmarshal(responseEntity).to[String].value.get.get

    println("Response string: " + responseString)

    fileIO.jsonToGameState(responseString)
  }

  def nextPlayer(): GameStateInterface = {
    val request = HttpRequest(uri = "http://localhost:8081/nextPlayer")
    val responseFuture = Http().singleRequest(request)

    val response = Await.result(responseFuture, 10.seconds)
    println("Response: " + response)

    val responseEntity = Await.result(response.entity.toStrict(2.seconds), 2.seconds)
    val responseString = Unmarshal(responseEntity).to[String].value.get.get

    println("Response string: " + responseString)

    fileIO.jsonToGameState(responseString)
  }

  def getAllowedPlayerForPlayerSelection: Vector[String] = {
    val request = HttpRequest(uri = "http://localhost:8081/getAllowedPlayerForPlayerSelection")
    val responseFuture = Http().singleRequest(request)

    val response = Await.result(responseFuture, 10.seconds)
    println("Response: " + response)
    response.entity.toString.split(",").toVector
  }

}
