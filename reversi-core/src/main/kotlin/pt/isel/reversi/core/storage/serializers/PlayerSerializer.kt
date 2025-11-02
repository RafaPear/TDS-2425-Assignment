package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.Player
import pt.isel.reversi.storage.Serializer

class PlayerSerializer: Serializer<Player, String> {
    val pieceTypeSerializer = PieceTypeSerializer()
    override fun serialize(obj: Player): String {
        val symbol = pieceTypeSerializer.serialize(obj.type)
        val points = obj.points

        return "$symbol,$points"
    }

    override fun deserialize(obj: String): Player {
        val (symbol, points) = obj.trim().split(",")
        val type = pieceTypeSerializer.deserialize(symbol.first())
        return Player(type, points.toInt())
    }
}