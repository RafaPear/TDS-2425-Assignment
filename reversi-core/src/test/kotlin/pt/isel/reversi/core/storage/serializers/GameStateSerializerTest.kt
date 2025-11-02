package pt.isel.reversi.core.storage.serializers

import org.junit.Test
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.storage.GameState

class GameStateSerializerTest {
    val testUnit = SerializerTestUnit(GameStateSerializer()) {
        val list = mutableListOf<GameState>()
        val sides = listOf(4, 6, 8, 10, 12, 14, 16)
        for (side in sides) {
            val pieces = mutableListOf<Piece>()
            for (i in 0 until side) {
                for (j in 0 until side) {
                    val cord = Coordinate(i, j)
                    val pieceType = PieceType.entries.random()
                    pieces += Piece(cord, pieceType)
                }
            }
            val board = Board(side, pieces)
            val currentPlayer = PieceType.entries.random()
            val players = listOf(
                Player(PieceType.BLACK),
                Player(PieceType.WHITE)
            )
            val gameState = GameState(players, currentPlayer, board)
            list += gameState
        }
        list
    }

    @Test
    fun `Test serialize and deserialize`() {
        testUnit.runTest()
    }
}