package pt.isel.reversi.core.game

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType

/**
 * Represents a player in the game.
 * @property type The type of piece the player uses.
 * @property points The current points of the player.
 */
data class Player(
    val type: PieceType,
    val points: Int = 0,
) {
    fun refresh(board: Board): Player {
        return Player(
            type = type,
            points = when (type) {
                PieceType.BLACK -> board.totalBlackPieces()
                PieceType.WHITE -> board.totalWhitePieces()
            },
        )
    }
}