package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.storage.GameState
import pt.isel.reversi.storage.Serializer

class GameStateSerializer: Serializer<GameState, String> {
    private val pieceSerializer = PieceSerializer()
    private val pieceTypeSerializer = PieceTypeSerializer()

    override fun serialize(obj: GameState): String {
        require(obj.lastPlayer != null) { "Cannot serialize a game with no last player." }

        val sb = StringBuilder()

        // append current player if only one is present
        sb.appendLine(pieceTypeSerializer.serialize(obj.lastPlayer))

        // append pieces
        for (piece in obj.pieces)
            sb.appendLine(pieceSerializer.serialize(piece))

        return sb.toString()
    }

    override fun deserialize(obj: String): GameState {
        var parts = obj.split("\n")

        val lastPlayer = pieceTypeSerializer.deserialize(parts[0].first())
            .also { parts = parts.drop(1) }

        val pieces = parts.map { pieceSerializer.deserialize(it) }

        return GameState(lastPlayer, pieces)
    }
}