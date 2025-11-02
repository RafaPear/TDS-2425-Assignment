package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.storage.GameState
import pt.isel.reversi.storage.Serializer

/**
 * Serializer for the GameState class, converting it to and from a String representation.
 */
class GameStateSerializer : Serializer<GameState, String> {
    private val pieceTypeSerializer = PieceTypeSerializer()
    private val boardSerializer = BoardSerializer()

    override fun serialize(obj: GameState): String {
        requireNotNull(obj.lastPlayer) { "lastPlayer cannot be null" }
        requireNotNull(obj.board) { "board cannot be null" }

        val sb = StringBuilder()

        sb.appendLine(pieceTypeSerializer.serialize(obj.lastPlayer))
        sb.appendLine(boardSerializer.serialize(obj.board))

        return sb.toString()
    }

    private fun getLastPlayerPart(parts: List<String>): PieceType? {
        if (parts.isEmpty()) return null
        val firstLine = parts[0]
        if (firstLine.isBlank() || firstLine.first().isWhitespace()) return null
        return pieceTypeSerializer.deserialize(firstLine.first())
    }

    private fun getBoardPart(parts: List<String>): Board? {
        if (parts.size < 2) return null
        if (parts[1].isBlank() || parts[1].first().isWhitespace()) return null
        val boardPart = parts.drop(1).joinToString("\n")
        return boardSerializer.deserialize(boardPart)
    }

    override fun deserialize(obj: String): GameState {
        val parts = obj.split("\n")

        val lastPlayer = getLastPlayerPart(parts)
        val board = getBoardPart(parts)

        require(lastPlayer != null) { "Corrupted Game Save: LastPlayer does not exist" }
        require(board != null) { "Corrupted Game Save: Board does not exist" }

        return GameState(
            lastPlayer = lastPlayer,
            board = board
        )
    }
}