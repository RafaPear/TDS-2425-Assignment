import pt.isel.Board
import kotlin.test.Test
import kotlin.test.assertFailsWith

class BoardTests {

    @Test
    fun `Create Board with side outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = -1)
            Board(side = 0)
            Board(side = 2)
            Board(side = 30)
        }
    }

    @Test
    fun `Create Board with odd side within range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 5)
            Board(side = 25)
        }
    }

    @Test
    fun `Create Piece with negative row fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Piece(row = -1, col = 1, 'w')
        }
    }

    @Test
    fun `Create Piece with negative col fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Piece(row = 1, col = -1, 'w')
        }
    }

    @Test
    fun `Create Piece with zero row fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Piece(row = 0, col = 1, 'w')
        }
    }

    @Test
    fun `Create Piece with zero col fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Piece(row = 1, col = 0, 'w')
        }
    }

    @Test
    fun `Create Piece with a third color fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board.Piece(row = 1, col = 0, 'y')
        }
    }

    @Test
    fun `get function with row outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            TODO()
        }
    }

    @Test
    fun `get function with col outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            TODO()
        }
    }

}