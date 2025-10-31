package pt.isel.reversi.core.storage

import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType

data class GameState(
    val lastPlayer: PieceType? = null,
    val pieces: List<Piece> = emptyList(),
    val id: String? = null
) {
    fun setId(newId: String): GameState {
        return GameState(
            lastPlayer = lastPlayer,
            pieces = pieces,
            id = newId
        )
    }

    fun addPiece(piece: Piece): GameState {
        return GameState(
            lastPlayer = piece.value,
            pieces = pieces + piece,
            id = id
        )
    }

    fun setLastPlayer(player: PieceType): GameState {
        return GameState(
            lastPlayer = player,
            pieces = pieces,
            id = id
        )
    }
}

