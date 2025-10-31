package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.storage.Serializer

class PieceTypeSerializer: Serializer<PieceType, Char> {
    override fun serialize(obj: PieceType): Char {
        return obj.symbol
    }

    override fun deserialize(obj: Char): PieceType {
        return PieceType.fromSymbol(obj) ?: throw IllegalArgumentException("Invalid piece symbol")
    }
}