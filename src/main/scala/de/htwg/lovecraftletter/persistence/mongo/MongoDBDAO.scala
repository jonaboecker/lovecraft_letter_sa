package de.htwg.lovecraftletter.persistence.mongo

import de.htwg.lovecraftletter.model.GameStateImpl.GameState
import de.htwg.lovecraftletter.model.GameStateInterface
import de.htwg.lovecraftletter.model.PlayerImpl.Player
import de.htwg.lovecraftletter.persistence.DBDAO
import org.mongodb.scala.*
import play.api.libs.json.{JsArray, JsObject, Json}

import scala.concurrent.Await
import scala.concurrent.duration.*

class MongoDBDAO() extends DBDAO {
  private val database_pw = sys.env.getOrElse("MONGO_ROOT_PASSWORD", "123")
  private val database_username = sys.env.getOrElse("MONGO_ROOT_USERNAME", "root")
  private val host = sys.env.getOrElse("MONGO_HOST", "host.docker.internal")
  private val port = sys.env.getOrElse("MONGO_PORT", "27017")
  private val uri: String = s"mongodb://$database_username:$database_pw@$host:$port/?authSource=admin"
  private val client: MongoClient = MongoClient(uri)
  private val db: MongoDatabase = client.getDatabase("ll")
  private val gameStateCollection: MongoCollection[Document] = db.getCollection("gameState")


  override def dropTables(): Unit = {
    Await.result(gameStateCollection.drop().toFuture(), 10.seconds)
  }

  override def createTables(): Unit = {
    // In MongoDB, collections are created automatically when you insert the first document
  }

  override def save(game: GameStateInterface): Unit = {
    dropTables()
    createTables()

    val gameStateDocument: Document = Document(
      "currentPlayer" -> game.currentPlayer,
      "drawPile" -> game.drawPile.mkString(","),
      "player" -> Json.toJson(
        for (x <- game.player.indices) yield {
          Json.obj(
            "name" -> game.player(x).name,
            "hand" -> game.player(x).hand,
            "discardPile" -> Json.toJson(game.player(x).discardPile),
            "inGame" -> game.player(x).inGame
          )
        }
      ).toString(),
      "currentCard" -> game.currentCard
    )
    //val gameStateDocument: Document = Document("currentPlayer" -> game.currentPlayer, "drawPile" -> game.drawPile, "playerAmount" -> game.player.length, "currentCard" -> game.currentCard)
    insertDocument(gameStateCollection, gameStateDocument)
  }

  override def load(oldGameState: GameStateInterface): GameStateInterface = {
    val gameDocument = Await.result(gameStateCollection.find().first().toFuture(), 10.seconds)

    val playersJsonArray = Json.parse(gameDocument.getString("player")).as[JsArray]

    GameState(
      gameDocument.getInteger("currentPlayer"),
      gameDocument.getString("drawPile").split(",").toList.map(_.toInt),
      playersJsonArray.value.map { playerJson =>
        val player = playerJson.as[JsObject]
        Player(
          (player \ "name").as[String],
          (player \ "hand").as[Int],
          (player \ "discardPile").as[List[Int]],
          (player \ "inGame").as[Boolean]
        )
      }.toList,
      gameDocument.getInteger("currentCard")
    )
  }

  override def closeDB(): Unit = {
    client.close()
  }

  private def insertDocument(collection: MongoCollection[Document], document: Document): Unit =
    Await.result(collection.insertOne(document).asInstanceOf[SingleObservable[Unit]].head(), 10.seconds)
}