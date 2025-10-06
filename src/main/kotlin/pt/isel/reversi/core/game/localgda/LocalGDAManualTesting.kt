package pt.isel.reversi.core.game.localgda

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.game.GameImpl
import pt.isel.reversi.core.game.MockGame
import pt.isel.reversi.core.game.Player
import pt.isel.reversi.core.game.data.GDAResult
import java.nio.file.Files

fun main() {
    val dataAccess = LocalGDA()
    Files.createTempDirectory("reversi-test-")
    // val path = dir.resolve("game${(0..100).random()}.txt").toString()
    val path = "game.txt"
    println("Using file: $path")
    var game: GameImpl = MockGame.EmptyPlayers(dataAccess, path)

    val resultsList = mutableListOf<GDAResult<*>>()

    resultsList += dataAccess.postGame(path, game)
    resultsList += dataAccess.postPiece(path, Piece(Coordinate(2, 3), PieceType.BLACK))

    game = MockGame.OnePlayer(dataAccess, path)
    resultsList += dataAccess.postGame(path, game)
    resultsList += dataAccess.postPiece(path, Piece(Coordinate(2, 3), PieceType.BLACK))

    game = MockGame(
        dataAccess, listOf(
            Player(PieceType.BLACK, 15, 17),
            Player(PieceType.WHITE, 15, 17)
        ), path, Board(8), target = false, isLocal = false
    )
    resultsList += dataAccess.postGame(path, game)
    resultsList += dataAccess.postPiece(path, Piece(Coordinate(2, 3), PieceType.BLACK))
    resultsList += dataAccess.postPiece(path, Piece(Coordinate(2, 3), PieceType.WHITE))
    resultsList += dataAccess.postPass(path, PieceType.WHITE)
    resultsList += dataAccess.postPass(path, PieceType.BLACK)

    val result = dataAccess.getBoard(path)
    resultsList += result
    if (result.data != null)
        game = game.copy(dataAccess, board = result.data)

    readln()
    resultsList += dataAccess.postGame(path, game)

    resultsList.forEach { println(it.toStringColored()) }
    readln()
}