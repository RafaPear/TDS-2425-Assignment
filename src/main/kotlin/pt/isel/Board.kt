package pt.isel

import kotlin.collections.find


const val SIDE_MIN = 4
const val SIDE_MAX = 26

/**
 * Represents a board game grid.
 *
 * @property pieces The list of pieces on the board.
 * @property side The size of the board (side x side).
 */

@ConsistentCopyVisibility
data class Board private constructor(
    private val side: Int,
    private val pieces: List<Piece>
): Iterable<Char> {

    constructor(side: Int) : this(side = side, emptyList())

    init {
        require(side in SIDE_MIN..SIDE_MAX) {
            "Side must be between $SIDE_MIN and $SIDE_MAX"
        }
        require(side % 2 == 0) {
            "Side must be even"
        }
    }

    /**
     * Represents a piece on the board.
     */
    private data class Piece(val row: Int, val col: Int, val value: Char)

    /**
     * Gets the piece at the specified row and column.
     * @return The piece at the specified position, or null if there is no piece.
     */
    operator fun get(row: Int, col: Char): Char? = this[row, col.toIntIndex()]

    /**
     * Gets the piece at the specified row and column.
     * @return The piece at the specified position, or null if there is no piece.
     */
    operator fun get(row: Int, col: Int): Char? {
        require(row in 1..side) {
            "Row must be between 1 and $side" }
        require(col in 1..side) {
            "Column must be between 1 and $side"
        }
        return pieces.find { it.row == row && it.col == col }?.value
    }

    /**
     * Changes the piece at the specified row and column from 'b' to 'w' or from 'w' to 'b'.
     * @return true if the piece was changed, false if there is no piece at the specified position.
     */
    fun changePiece(row: Int, col: Char): Board =
        changePiece(row, col.toIntIndex())

    /**
     * Changes the piece at the specified row and column from 'b' to 'w' or from 'w' to 'b'.
     * @return true if the piece was changed, false if there is no piece at the specified position.
     */
    fun changePiece(row: Int, col: Int): Board {
        require(row in 1..side) {
            "Row must be between 1 and $side"
        }
        require(col in 1..side) {
            "Column must be between 1 and $side}"
        }
        val value = this[row,col] ?: return this
        val newValue = if (value == 'b') 'w' else 'b'
        return this.copy(pieces = pieces.map {piece ->
            if (piece.row == row && piece.col == col)
                piece.copy(value = newValue)
            else
                piece
        })
    }

    /**
     * Adds a piece to the board at the specified row and column.
     */
    fun addPiece(row: Int, col: Char, value: Char): Board =
        this.addPiece(row, col.toIntIndex(), value)

    /**
     * Adds a piece to the board at the specified row and column.
     */
    fun addPiece(row: Int, col: Int, value: Char): Board {
        val value = value.lowercase()[0]
        require(row in 1..side) {
            "Row must be between 1 and $side"
        }
        require(col in 1..side) {
            "Column must be between 1 and $side"
        }
        require(value == 'b' || value == 'w') {
            "Value must be 'b' or 'w'"
        }
        if (this[row, col] == null) return this
        return this.copy(pieces = pieces + Piece(row, col, value))
    }

    /**
     * Converts a column character ('a', 'b', ...) to its corresponding integer index.
     */
    private fun Char.toIntIndex(): Int {
        val colLower = this.lowercase()[0]
        require(colLower in 'a'..'a' + side - 1) {
            "Column must be between 'a' and '${'a' + side - 1}'" }
        return colLower - 'a' + 1
    }

    /**
     * Starts the board with the initial pieces in the center.
     * @return A list of the initial pieces.
     */
    fun startPieces(): Board {
        val mid = side / 2
        return this.copy(pieces = listOf(
            Piece(row = mid, col = mid, value = 'w'),
            Piece(row = mid + 1, col = mid + 1, value = 'w'),
            Piece(row = mid, col = mid + 1, value = 'b'),
            Piece(row = mid + 1, col = mid, value = 'b')
        ))
    }

    /**
     * Returns an iterator of the value of pieces on the board.
     */
    override fun iterator(): Iterator<Char> = object : Iterator<Char> {
        private val it = pieces.iterator()
        override fun hasNext() = it.hasNext()
        override fun next() = it.next().value
    }
}