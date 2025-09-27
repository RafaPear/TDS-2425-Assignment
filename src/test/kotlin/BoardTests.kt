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
            Board(side = 8)[-1, 1]
            Board(side = 8)[0, 1]
            Board(side = 8)[27, 1]
        }
    }

    @Test
    fun `get function with col outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8)[1, -1]
            Board(side = 8)[1, 0]
            Board(side = 8)[1, 27]
            Board(side = 8)[1, '@']
            Board(side = 8)[1, '[']
        }
    }

    @Test
    fun `changePiece function with row outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8).changePiece(row = -1, col = 1)
            Board(side = 8).changePiece(row = 0, col = 1)
            Board(side = 8).changePiece(row = 27, col = 1)
        }
    }

    @Test
    fun `changePiece function with col outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8).changePiece(row = 1, col = -1)
            Board(side = 8).changePiece(row = 1, col = 0)
            Board(side = 8).changePiece(row = 1, col = 27)
            Board(side = 8).changePiece(row = 1, col = '@')
            Board(side = 8).changePiece(row = 1, col = '[')
        }
    }

    @Test
    fun `addPiece function with row outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8).addPiece(row = -1, col = 1, value = 'w')
            Board(side = 8).addPiece(row = 0, col = 1, value = 'w')
            Board(side = 8).addPiece(row = 27, col = 1, value = 'w')
        }
    }

    @Test
    fun `addPiece function with col outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8).addPiece(row = 1, col = -1, value = 'w')
            Board(side = 8).addPiece(row = 1, col = 0, value = 'w')
            Board(side = 8).addPiece(row = 1, col = 27, value = 'w')
            Board(side = 8).addPiece(row = 1, col = '@', value = 'w')
            Board(side = 8).addPiece(row = 1, col = '[', value = 'w')
        }
    }

    @Test
    fun `addPiece function with a third color fails`() {
        assertFailsWith<IllegalArgumentException> {
            Board(side = 8).addPiece(row = 1, col = 1, value = 'y')
        }
    }

}