package de.htwg.lovecraftletter
package controller
package controllerImpl

import model.BoardImpl.Board
import model.GameStateInterface
import model.PlayerInterface
import model.*
import util.*
import de.htwg.lovecraftletter.model.FileIO.FileIOInterface
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.*
import akka.http.scaladsl.server.Route
import de.htwg.lovecraftletter.model.FileIO.FileIOImpl.FileIOJSON
import play.api.libs.json.Json

import scala.concurrent.{ExecutionContextExecutor, Future}


case class Controller(
                       var state: GameStateInterface,
                       var controllerState: (controllState, String),
                       var userInput: Int
                     ) extends ControllerInterface {
  private val undoManager = new UndoManager[GameStateInterface]
  private var allowedInput: Vector[String] = Vector("1", "2")
  private var effectHandlerSelection: Vector[Int] = Vector(-999)

  //contr.setVarState(state.updateCurrentPlayer(input))
  //contr.setVarControllerState(controllState.initGetPlayerName, "")


  implicit val system: ActorSystem = ActorSystem("ActorSystemController")
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
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


  val bindingFuture: Future[Http.ServerBinding] = Http().newServerAt("localhost", 8081).bind(route)
  

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
    Initializer(state).playerAmount(input)
  }

  override def playerName(input: String): Unit = {
    Initializer(state).playerName(input)
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
    executeEffectHandler(_.initializeEffectHandler)
  }

  override def playerChosen(chosenPlayer: Int): GameStateInterface = {
    effectHandlerSelection = Vector(effectHandlerSelection(0), chosenPlayer - 1)
    executeEffectHandler(_.strategy)
  }

  override def investgatorGuessed(guess: Int): GameStateInterface = {
    effectHandlerSelection = Vector(effectHandlerSelection(0), effectHandlerSelection(1), guess)
    executeEffectHandler(_.guessTeammateHandcard2)
  }
  private def executeEffectHandler(effectHandlerMethod: EffectHandler => GameStateInterface): GameStateInterface = {
    resetControllerState()
    state = effectHandlerMethod(EffectHandler(state, effectHandlerSelection))
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

  override def getAllowedPlayerForPlayerSelection: Vector[String] = {
    val res =
    rekGetAllowedPlayerForPlayerSelection(1, state.player, Vector[String]())
    res
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
    fileIO.save(state)
  }

  override def load(using fileIO: FileIOInterface): Unit = {
    state = fileIO.load(state)
    notifyObservers
  }

  override def resetGame(): Unit = {
    Initializer(state).initialize()
  }
}
