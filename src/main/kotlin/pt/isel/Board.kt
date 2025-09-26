package pt.isel

/**
 * Represents a board game grid.
 *
 * @property cell A nullable Boolean representing the state of a cell in the grid.
*                  true - white,
 *                 false - black,
 *                 null - blank.
 */
class Board(private val side: Int) {

    private val grid: List<Piece> = emptyList()
    data class Piece(val row: Int, val col: Int, var value: Char)

    operator fun get(row: Int, col: Char): Piece {
        require(col.lowercase()[0] in 'a'..'a' + side - 1) {
            "Column must be between 'a' and '${'a' + grid.size - 1}'" }
        TODO()
    }

    operator fun get(row: Int, col: Int): Piece {
        require(row in 1..grid.size) {
            "Row must be between 1 and ${grid.size}" }
        TODO()
    }

    fun changePiece(row: Int, col: Char, value: Char) {
        TODO()
    }

    fun addPiece(row: Int, col: Char, value: Char) {
        TODO()
    }
}

fun s() {
    val b = Board(8)
}