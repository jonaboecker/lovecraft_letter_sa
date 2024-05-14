package de.htwg.lovecraftletter.persistence.slick

import de.htwg.lovecraftletter.model.GameStateImpl.GameState
import de.htwg.lovecraftletter.model.GameStateInterface
import de.htwg.lovecraftletter.model.PlayerImpl.Player
import de.htwg.lovecraftletter.persistence.DBDAO
import slick.jdbc.PostgresProfile.api.*

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.*

class SlickDBDAO() extends DBDAO {
  var db: Database = null
  openDB()
  val gameStates = TableQuery[GameStateTable]
  val players = TableQuery[PlayerTable]

  def openDB(): Unit = {
    db = Database.forURL(
      url = "jdbc:postgresql://localhost:5432/postgres",
      driver = "org.postgresql.Driver",
      user = "user",
      password = "password"
    )
  }

  override def dropTables(): Unit = {
    val dropAction = DBIO.seq(
      gameStates.schema.drop,
      players.schema.drop
    )
    db.run(dropAction)
  }

  override def createTables(): Unit = {
    val createAction = DBIO.seq(
      gameStates.schema.create,
      players.schema.create
    )
    db.run(createAction)
  }

  override def save(game: GameStateInterface): Unit = {
    openDB()
    dropTables()
    createTables()
    // players aus datenbank löschen
    val deleteAction = players.delete
    db.run(deleteAction)


    var i = 1
    for (p <- game.player) {
      val insertAction = DBIO.seq(
        players += PlayerRow(p.name, p.hand, {
          for (x <- p.discardPile.indices)
            yield {
              p.discardPile(x)
            }
        }.toString(), p.inGame, i)
      )
      db.run(insertAction)
      i += 1
    }

    val drawPile: String = {
      for (x <- game.drawPile.indices)
        yield {
          game.drawPile(x)
        }
    }.toString()
    println("DrawPile: " + drawPile)
    val insertAction = DBIO.seq(
      gameStates += GameStateRow(game.currentPlayer, drawPile, game.player.length, 1, 2, 3, 4, 5, 6, game.currentCard),
    )
    db.run(insertAction)
  }

  override def load(oldGameState: GameStateInterface): GameStateInterface = {
    openDB()
    val queryAction = gameStates.result
    val resultFuture: Future[Seq[GameStateRow]] = db.run(queryAction)
    val gameStateRows: Seq[GameStateRow] = Await.result(resultFuture, 10.seconds)

    gameStateRows.map { row =>
      val currentPlayer = row.currentPlayer
      println("row: " + row.drawPile)
      //val drawPile = row.drawPile.split("t").map(_.stripSuffix("t").toInt).toList
      val drawPile: List[Int] = row.drawPile.stripPrefix("Vector(").stripSuffix(")").split(", ").map(_.toInt).toList
      println("List of draw pile: " + drawPile)
      val playerIds = List(row.player1, row.player2, row.player3, row.player4, row.player5, row.player6)
      val currentCard = row.currentCard

      // Laden der Player-Instanzen aus der Datenbank
      val players: List[Player] = {
        for (i <- 1 to row.playerAmount)
          yield {
            getPlayerById(i)
          }
      }.toList
      println(GameState(currentPlayer, drawPile, players, currentCard))
      GameState(currentPlayer, drawPile, players, currentCard)
    }.headOption.getOrElse(oldGameState)
  }
  
  private def getPlayerById(id: Int): Player = {
    val queryAction = players.filter(_.id === id).result
    val resultFuture: Future[Seq[PlayerRow]] = db.run(queryAction)
    val playerRows: Seq[PlayerRow] = Await.result(resultFuture, 10.seconds)
    playerRows.map { playerRow =>
      Player(playerRow.name, playerRow.hand, playerRow.discardPile.stripPrefix("Vector(").stripSuffix(")").split(", ").map(_.toInt).toList, playerRow.inGame)
    }.headOption.getOrElse(throw new Exception("Player not found"))
  }

  override def closeDB(): Unit = {
    db.close()
  }
}
