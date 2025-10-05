package pt.isel.reversi.core.board

/**
 * Represents the type of piece on the board.
 */
enum class PieceType(val symbol: Char) {
    BLACK('#'),
    WHITE('@');

    /**
     * Swaps the piece type to the opposite color.
     * @return The opposite PieceType.
     */
    fun swap(): PieceType =
        if (this == BLACK) WHITE
        else BLACK
}
