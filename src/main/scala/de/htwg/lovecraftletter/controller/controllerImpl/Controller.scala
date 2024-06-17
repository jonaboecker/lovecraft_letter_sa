package de.htwg.lovecraftletter
package controller
package controllerImpl

import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.unmarshalling.Unmarshal
import de.htwg.lovecraftletter.model.BoardImpl.Board
import de.htwg.lovecraftletter.model.FileIO.FileIOImpl.FileIOJSON
import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import de.htwg.lovecraftletter.model.*
import de.htwg.lovecraftletter.persistence.mongo.MongoDBDAO
import de.htwg.lovecraftletter.persistence.slick.SlickDBDAO
import de.htwg.lovecraftletter.util.*
import play.api.libs.json.Json

import scala.concurrent.duration.*
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import akka.actor.ActorSystem
import akka.stream.scaladsl.*
import akka.stream.{ActorMaterializer, OverflowStrategy}
import akka.stream.scaladsl.Sink

import scala.concurrent.{Await, Future, TimeoutException}
import scala.concurrent.duration.*
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.*

import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.clients.consumer.KafkaConsumer
import java.util.Properties
import scala.collection.JavaConverters._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global


case class Controller(
                       var state: GameStateInterface,
                       var controllerState: (controllState, String),
                       var userInput: Int
                     ) extends ControllerInterface {
  private val undoManager = new UndoManager[GameStateInterface]
  private var allowedInput: Vector[String] = Vector("1", "2")
  private var effectHandlerSelection: Vector[Int] = Vector(-999)


  implicit val system: ActorSystem = ActorSystem("ActorSystemController")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  //private val fileIOToSave: SlickDBDAO = SlickDBDAO()
  private val fileIOToSave: MongoDBDAO = MongoDBDAO()
  val fileIO: FileIOJSON = FileIOJSON()

  private val route: Route = {
    path("notifyObservers") {
      get {
        notifyObservers
        complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Notified Observers"))
      }
    } ~
      path("drawCard") {
        get {
          drawCard
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "Card drawn"))
        }
      } ~
      path("setVarGameState") {
        post {
          entity(as[String]) { input =>
            val newState = fileIO.jsonToGameState(input)
            setVarState(newState)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "State updated"))
          }
        }
      } ~
      path("setVarControllerState") {
        post {
          entity(as[String]) { input =>
            val json = Json.parse(input)
            val cs = (json \ "cs").as[Int]
            val s = (json \ "s").as[String]
            setVarControllerState(controllState.fromOrdinal(cs), s)
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "ControllerState updated"))
          }
        }
      } ~
      path("eliminatePlayer") {
        post {
          entity(as[String]) { input =>
            val json = Json.parse(input)
            val player = (json \ "player").as[String].toInt
            eliminatePlayer(player)
            val stateJsonString: String = fileIO.gameStateToJSON(state)
            complete(HttpEntity(ContentTypes.`application/json`, stateJsonString))
          }
        }
      } ~
      path("playAnotherCard") {
        get {
          playAnotherCard
          val stateJsonString: String = fileIO.gameStateToJSON(state)
          complete(HttpEntity(ContentTypes.`application/json`, stateJsonString))
        }
      } ~
      path("playerWins") {
        post {
          entity(as[String]) { input =>
            val json = Json.parse(input)
            val player = (json \ "player").as[String].toInt
            playerWins(player)
            val stateJsonString: String = fileIO.gameStateToJSON(state)
            complete(HttpEntity(ContentTypes.`application/json`, stateJsonString))
          }
        }
      } ~
      path("nextPlayer") {
        get {
          nextPlayer
          val stateJsonString: String = fileIO.gameStateToJSON(state)
          complete(HttpEntity(ContentTypes.`application/json`, stateJsonString))
        }
      } ~
      path("getAllowedPlayerForPlayerSelection") {
        get {
          val allowedPlayers = getAllowedPlayerForPlayerSelection
          complete(HttpEntity(ContentTypes.`application/json`, Json.toJson(allowedPlayers).toString()))
        }
      }
  }


  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("0.0.0.0", 8081).bind(route)


  override def setVarUserInput(input: Int): Unit = userInput = input

  override def setVarControllerState(cs: controllState, s: String): Unit = controllerState = (cs, s)

  override def setVarState(state: GameStateInterface): Unit = this.state = state

  override def getVarControllerState: (controllState, String) = controllerState

  override def getVarAllowedInput: Vector[String] = allowedInput

  override def runLL(): Unit = {
    controllerState = (controllState.initGetPlayerAmount, "")
    notifyObservers
  }

  override def playerAmount(input: Int): Unit = {
    val stateJson = fileIO.gameStateToJSON(state)
    val jsString = Json.obj(
      "state" -> stateJson,
      "player" -> input
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://host.docker.internal:8082/playerAmount",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)


    val response = Await.result(responseFuture, 10.seconds)
    // ("Response: " + response)
  }

  override def playerName(input: String): Unit = {
    val stateJson = fileIO.gameStateToJSON(state)
    val jsString = Json.obj(
      "state" -> stateJson,
      "player" -> input
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://host.docker.internal:8082/playerName",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)


    val response = Await.result(responseFuture, 10.seconds)
    // ("Response: " + response)
  }

  override def nextPlayer: GameStateInterface = {
    state = state.nextPlayer
    while (!state.player(state.currentPlayer).inGame) {
      state = state.nextPlayer
    }
    MadHandler.draw
    state
  }

  override def getPlayerName: String = {
    state.player(state.currentPlayer).name
  }

  override def drawCard: GameStateInterface = {
    state = state.drawCard
    state
  }

  override def checkForCard7or15(playedCard: Int): Int = {
    val currentPlayer = state.player(state.currentPlayer)
    val currentCardIsSpecial = state.currentCard == 7 || state.currentCard == 15
    val handCardIsSpecial = currentPlayer.hand == 7 || currentPlayer.hand == 15
    val oneCardIsSpecial = currentCardIsSpecial || handCardIsSpecial
    val bothCardsAreSpecial = currentCardIsSpecial && handCardIsSpecial

    if (oneCardIsSpecial && !bothCardsAreSpecial) {
      val cardToPlay = if (currentPlayer.madCheck() > 0 && (state.currentCard == 15 || currentPlayer.hand == 15)) {
        playedCard
      } else if (currentCardIsSpecial && isCardForcedToPlay(currentPlayer.hand)) {
        1
      } else if (handCardIsSpecial && isCardForcedToPlay(state.currentCard)) {
        2
      } else {
        playedCard
      }
      return cardToPlay
    }
    playedCard
  }

  private def isCardForcedToPlay(card: Int): Boolean = {
    card match {
      case 5 | 6 | 8 | 13 | 14 | 16 =>
        controllerState = (controllState.informOverPlayedEffect, "Du musst Karte " + card + " spielen")
        notifyObservers
        resetControllerState()
        true
      case _ => false
    }
  }

  override def makeTurn: GameStateInterface = {
    undoManager.doStep(state, PlayCommand(this))
    state
  }

  override def playCard: GameStateInterface = {
    val card = checkForCard7or15(userInput)
    card match
      case 1 => MadHandler.play
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    state
  }

  override def playAnotherCard: GameStateInterface = {
    state = drawCard
    notifyObservers
    controllerState = (controllState.getInputToPlayAnotherCard, "")
    notifyObservers
    state
  }

  override def playAnotherCard2(otherCard: Int): GameStateInterface = {
    resetControllerState()
    val card = checkForCard7or15(otherCard)
    card match
      case 1 => MadHandler.play
      case 2 =>
        state = state.swapHandAndCurrent
        MadHandler.play
    // handle discarded card
    state
  }

  override def undoStep: GameStateInterface = {
    state = undoManager.undoStep(state)
    notifyObservers
    state
  }

  override def redoStep: GameStateInterface = {
    state = undoManager.redoStep(state)
    notifyObservers
    state
  }

  override def playEffect(selectedEffect: Int): GameStateInterface = {
    effectHandlerSelection = Vector(selectedEffect)
    executeEffectHandler("playEffect")
  }

  override def playerChosen(chosenPlayer: Int): GameStateInterface = {
    effectHandlerSelection = Vector(effectHandlerSelection(0), chosenPlayer - 1)
    executeEffectHandler("strategy")
  }

  override def investgatorGuessed(guess: Int): GameStateInterface = {
    effectHandlerSelection = Vector(effectHandlerSelection(0), effectHandlerSelection(1), guess)
    executeEffectHandler("guessTeammateHandcard2")
  }

  private def executeEffectHandler(effectHandlerMethod: String): GameStateInterface = {
    resetControllerState()

    val stateJson = fileIO.gameStateToJSON(state)
    val jsString = Json.obj(
      "state" -> stateJson,
      "selection" -> effectHandlerSelection,
      "method" -> effectHandlerMethod
    ).toString
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://host.docker.internal:8083/init",
      entity = HttpEntity(ContentTypes.`application/json`, jsString)
    )
    val responseFuture = Http().singleRequest(request)


    val response = Await.result(responseFuture, 10.seconds)
    // println("Response: " + response)
    val responseEntity = Await.result(response.entity.toStrict(2.seconds), 2.seconds)
    val responseString = Unmarshal(responseEntity).to[String].value.get.get

    // println("Response string: " + responseString)

    state = fileIO.jsonToGameState(responseString)
    state
  }

  override def checkUponWin: Boolean = {
    val players = state.player.filter(_.inGame)
    if (players.length == 1) {
      // state.player.indexOf(players(0))
      playerWins(state.player.indexOf(players.head))
      return true
    }
    false
  }

  override def playerWins(winningPlayer: Int): GameStateInterface = {
    controllerState =
      (controllState.playerWins, state.player(winningPlayer).name)
    notifyObservers
    resetGame()
    state
  }



  // Kafka Producer erstellen
  val props = new Properties()
  props.put("bootstrap.servers", "http://host.docker.internal:9092")
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  private val producer = new KafkaProducer[String, String](props)

  def getAllowedPlayerForPlayerSelectionKafka: Vector[String] = {
    val future = Future {
      streamGetAllowedPlayerForPlayerSelection(state.player, Vector[String]())
    }
    // Nachricht an Kafka senden
    val record = new ProducerRecord[String, String]("allowedPlayersTopic", "getAllowedPlayers", "")
    producer.send(record)
    producer.close()

    // Kafka Consumer erstellen
    val props2 = new Properties()
    props2.put("bootstrap.servers", "http://host.docker.internal:9092")
    props2.put("group.id", "test")
    props2.put("enable.auto.commit", "true")
    props2.put("auto.commit.interval.ms", "1000")
    props2.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props2.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val consumer = new KafkaConsumer[String, String](props2)

    // Auf Nachricht von Kafka warten
    consumer.subscribe(java.util.Collections.singletonList("allowedPlayersTopic"))

    val records = consumer.poll(5000).asScala
    records.map(record => record.value()).toVector
  }

  private def streamGetAllowedPlayerForPlayerSelectionKafka(playerList: List[PlayerInterface], allowedPlayers: Vector[String]): Vector[String] = {
    // Kafka Consumer erstellen
    val props = new Properties()
    props.put("bootstrap.servers", "http://host.docker.internal:9092")
    props.put("group.id", "test")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val consumer = new KafkaConsumer[String, String](props)

    // Auf Nachricht von Kafka warten
    consumer.subscribe(java.util.Collections.singletonList("allowedPlayersTopic"))
    val records = consumer.poll(30000).asScala
    for (record <- records) {
      if (record.key() == "getAllowedPlayers") {
        // Logik zur Generierung der erlaubten Spieler
        val source = Source(state.player.zipWithIndex)
        val flow = Flow[(PlayerInterface, Int)].filter { case (player, index) =>
          player.inGame && player != state.player(state.currentPlayer) &&
            state.player(index).discardPile.head != 4 &&
            state.player(index).discardPile.head != 12
        }.map { case (player, index) => (player, index.+(1)) }.map { case (player, index) => index.toString }
        val sink = Sink.seq[String]
        val stream = source.via(flow).toMat(sink)(Keep.right)
        val futureResult = stream.run()
        val result = Await.result(futureResult, 10.seconds)
        val allowedPlayers = result.toVector

        // Nachricht an Kafka senden
        val props2 = new Properties()
        props2.put("bootstrap.servers", "http://host.docker.internal:9092")
        props2.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        props2.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        val producer = new KafkaProducer[String, String](props2)
        val record = new ProducerRecord[String, String]("allowedPlayersTopic", "allowedPlayers", allowedPlayers.mkString(","))
        producer.send(record)
        producer.close()
      }
    }

    consumer.close()
    Vector()
  }

  override def rekGetAllowedPlayerForPlayerSelection(
                                                      counter: Int,
                                                      playerList: List[PlayerInterface],
                                                      allowedPlayers: Vector[String]
                                                    ): Vector[String] = {
    playerList match {
      case Nil => allowedPlayers
      case head :: tail =>
        val isPlayerAllowed = head.inGame && head != state.player(state.currentPlayer) &&
          state.player(counter - 1).discardPile.head != 4 &&
          state.player(counter - 1).discardPile.head != 12
        val updatedAllowedPlayers = if (isPlayerAllowed) allowedPlayers.appended(counter.toString) else allowedPlayers
        rekGetAllowedPlayerForPlayerSelection(counter + 1, tail, updatedAllowedPlayers)
    }
  }

  override def getAllowedPlayerForPlayerSelection: Vector[String] = {
    //val res = rekGetAllowedPlayerForPlayerSelection(1, state.player, Vector[String]())
    val res = streamGetAllowedPlayerForPlayerSelection(state.player, Vector[String]())
    res
  }

  private def streamGetAllowedPlayerForPlayerSelection(playerList: List[PlayerInterface], allowedPlayers: Vector[String]): Vector[String] = {
    val source = Source(state.player.zipWithIndex)
    val flow = Flow[(PlayerInterface, Int)].filter { case (player, index) =>
      player.inGame && player != state.player(state.currentPlayer) &&
        state.player(index).discardPile.head != 4 &&
        state.player(index).discardPile.head != 12
    }.map { case (player, index) => (player, index.+(1)) }.map { case (player, index) => index.toString }
    val sink = Sink.seq[String]
    val stream = source.via(flow).toMat(sink)(Keep.right)
    val futureResult = stream.run()
    val result = Await.result(futureResult, 10.seconds)
    result.toVector
  }

  object MadHandler {
    def draw: GameStateInterface =
      if (
        !isPlayerInvincible && state.player(state.currentPlayer).isPlayerMad
      ) drawMad
      else drawNormal

    private def isPlayerInvincible = {
      state.player(state.currentPlayer).discardPile.head == 12
    }

    private def drawNormal = {
      drawCard
    }

    private def drawMad: GameStateInterface = {
      val tempCurrentPlayer = state.currentPlayer
      for (x <- 0 until state.player(tempCurrentPlayer).madCheck()) {
        if (state.player(tempCurrentPlayer).inGame) {
          drawCard
          if (state.currentCard > 8) {
            eliminatePlayer(tempCurrentPlayer)
            nextPlayer
          } else {
            state = state.playCard
          }
        }
      }
      drawCard

      state
    }

    def play: GameStateInterface =
      if (state.player(state.currentPlayer).madCheck() > 0) playMad
      else playNormal

    private def playNormal = {
      state = state.playCard
      playEffect(1)
    }

    private def playMad = {
      state = state.playCard
      playEffect(if (state.currentCard > 8) 2 else 1)
    }
  }

  override def eliminatePlayer(player: Int): GameStateInterface = {
    state = state.eliminatePlayer(player)
    controllerState =
      (controllState.tellEliminatedPlayer, state.player(player).name)
    notifyObservers
    checkUponWin
    state
  }

  override def handle: String = StateHandler.handle

  object StateHandler {
    def handle: String = {
      controllerState(0) match
        case controllState.standard => getBoard
        case controllState.initGetPlayerAmount =>
          allowedInput = Vector("3", "4", "5", "6")
          "Bitte Spieleranzahl zwischen 3 und 6 angeben"
        case controllState.initGetPlayerName =>
          allowedInput = Vector("")
          "Bitte Name für Spieler " + (state.player.length + 1) + " angeben"
        case controllState.tellEliminatedPlayer =>
          "Spieler " + controllerState(1) + " wurde eliminiert"
        case controllState.playerWins =>
          allowedInput = Vector("j", "n")
          "Spieler " + controllerState(1) + " hat die Runde gewonnen\nEs wird eine neue Runde gestartet."
        case controllState.getEffectedPlayer =>
          allowedInput = getAllowedPlayerForPlayerSelection
          "Waehle einen Spieler auf den du deine Aktion anwenden willst " + allowedInput.toString()
        case controllState.getInvestigatorGuess =>
          allowedInput = Vector("0", "2", "3", "4", "5", "6", "7", "8")
          "Welchen Wert der Handkarte raetst du (0|2-8)"
        case controllState.informOverPlayedEffect => controllerState(1)
        case controllState.getInputToPlayAnotherCard =>
          allowedInput = Vector("1", "2")
          "Welche Karte moechtest du spielen? (1|2)"
    }

    def getBoard: String = {
      val currentPlayer = state.player(state.currentPlayer)
      val board = Board(3, Vector(
        state.currentCard,
        currentPlayer.hand,
        currentPlayer.discardPile.head
      ), 1)

      "\n" + getPlayerName + " ist an der Reihe" + isCurrentPlayerMad + "\n" +
        board.toString + "\nWelche Karte moechtest du spielen? (1|2)"
    }

    private def isCurrentPlayerMad: String = {
      if (state.player(state.currentPlayer).madCheck() > 0) {
        return " und wahnsinnig"
      }
      ""
    }
  }

  override def resetControllerState(): Unit = {
    controllerState = (controllState.standard, "")
  }

  override def save(using fileIO: FileIOInterface): Unit = {
    fileIOToSave.save(state)
  }

  override def load(using fileIO: FileIOInterface): Unit = {
    state = fileIOToSave.load(state)
    notifyObservers
  }

  override def resetGame(): Unit = {
    val stateJsonString = fileIO.gameStateToJSON(state)
    val request = HttpRequest(
      method = HttpMethods.POST,
      uri = "http://host.docker.internal:8082/initialize",
      entity = HttpEntity(ContentTypes.`application/json`, stateJsonString)
    )
    val responseFuture = Http().singleRequest(request)


    val response = Await.result(responseFuture, 10.seconds)
    // ("Response: " + response)
  }


}
