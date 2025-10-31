package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.Game
import pt.isel.reversi.core.GameImpl
import pt.isel.reversi.core.storage.GameFile
import pt.isel.reversi.storage.Serializer

class GameFileSerializer: Serializer<GameFile, String> {
    private val pieceSerializer = PieceSerializer()
    private val pieceTypeSerializer = PieceTypeSerializer()

    override fun serialize(obj: GameFile): String {
        require(obj.lastPlayer != null) { "Cannot serialize a game with no last player." }

        val sb = StringBuilder()

        // append current player if only one is present
        sb.appendLine(pieceTypeSerializer.serialize(obj.lastPlayer))

        // append pieces
        for (piece in obj.pieces)
            sb.appendLine(pieceSerializer.serialize(piece))

        return sb.toString()
    }

    override fun deserialize(obj: String): GameFile {
        var parts = obj.split("\n")

        val lastPlayer = pieceTypeSerializer.deserialize(parts[0].first())
            .also { parts = parts.drop(1) }

        val pieces = parts.map { pieceSerializer.deserialize(it) }

        return GameFile(lastPlayer, pieces)
    }
}