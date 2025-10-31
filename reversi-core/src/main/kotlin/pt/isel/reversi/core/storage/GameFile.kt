package pt.isel.reversi.core.storage

import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType

data class GameFile(
    val lastPlayer: PieceType? = null,
    val pieces: List<Piece> = emptyList(),
    val id: String? = null
) {
    fun setId(newId: String): GameFile {
        return GameFile(
            lastPlayer = lastPlayer,
            pieces = pieces,
            id = newId
        )
    }

    fun addPiece(piece: Piece): GameFile {
        return GameFile(
            lastPlayer = piece.value,
            pieces = pieces + piece,
            id = id
        )
    }

    fun setLastPlayer(player: PieceType): GameFile {
        return GameFile(
            lastPlayer = player,
            pieces = pieces,
            id = id
        )
    }
}

