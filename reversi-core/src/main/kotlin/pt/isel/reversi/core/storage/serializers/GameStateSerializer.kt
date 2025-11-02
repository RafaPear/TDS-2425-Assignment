package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.Player
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
    private val playerSerializer = PlayerSerializer()

    val playersLine = 0
    val lastPlayerLine = 1
    val boardStartLine = 2

    override fun serialize(obj: GameState): String {
        requireNotNull(obj.lastPlayer) { "lastPlayer cannot be null" }
        requireNotNull(obj.board) { "board cannot be null" }

        val sb = StringBuilder()

        if (obj.players.isEmpty()) {
            sb.appendLine()
        } else {
            for (player in obj.players) {
                sb.append(playerSerializer.serialize(player))
                sb.append(";")
            }
            sb.appendLine()
        }

        sb.appendLine(pieceTypeSerializer.serialize(obj.lastPlayer))
        sb.appendLine(boardSerializer.serialize(obj.board))

        return sb.toString()
    }

    private fun getPlayers(parts: List<String>): List<Player> {
        if (parts.size + 1 < playersLine) return emptyList()
        val playersLineContent = parts[playersLine]
        if (playersLineContent.isBlank() || playersLineContent.first().isWhitespace())
            return emptyList()

        val playerStrings = playersLineContent.split(";")
        val players = mutableListOf<Player>()

        for (player in playerStrings) {
            if (player.isNotBlank())
                players += playerSerializer.deserialize(player)
        }
        return players
    }

    private fun getLastPlayerPart(parts: List<String>): PieceType? {
        if (parts.size + 1 < lastPlayerLine) return null
        val firstLine = parts[lastPlayerLine]
        if (firstLine.isBlank() || firstLine.first().isWhitespace()) return null
        return pieceTypeSerializer.deserialize(firstLine.first())
    }

    private fun getBoardPart(parts: List<String>): Board? {
        if (parts.size + 1 < boardStartLine) return null
        if (parts[boardStartLine].isBlank() || parts[boardStartLine].first().isWhitespace()) return null
        val boardPart = parts.drop(boardStartLine).joinToString("\n")
        return boardSerializer.deserialize(boardPart)
    }

    override fun deserialize(obj: String): GameState {
        val parts = obj.split("\n")

        val players = getPlayers(parts)
        val lastPlayer = getLastPlayerPart(parts)
        val board = getBoardPart(parts)

        require(lastPlayer != null) { "Corrupted Game Save: LastPlayer does not exist" }
        require(board != null) { "Corrupted Game Save: Board does not exist" }

        return GameState(
            players = players,
            lastPlayer = lastPlayer,
            board = board
        )
    }
}