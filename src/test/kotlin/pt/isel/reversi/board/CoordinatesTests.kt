package pt.isel.reversi.board

import kotlin.test.Test
import kotlin.test.assertFailsWith

class CoordinatesTests {
    @Test
    fun `Create Coordinates with col as Char outside range fails`() {
        assertFailsWith<IllegalArgumentException> {
            Coordinates(3, '1')
            Coordinates(5, '@')
            Coordinates(10, '{')
        }
    }
    @Test
    fun `Create Coordinates with col as Char`() {
        val coord1 = Coordinates(3, 'a')
        assert(coord1.row == 3 && coord1.col == 1)

        val coord2 = Coordinates(5, 'D')
        assert(coord2.row == 5 && coord2.col == 4)

        val coord3 = Coordinates(10, 'z')
        assert(coord3.row == 10 && coord3.col == 26)
    }

    @Test
    fun `Test equals method`() {
        val coord1 = Coordinates(2, 3)
        val coord2 = Coordinates(2, 3)
        val coord3 = Coordinates(3, 2)
        assert(coord1 == coord2)
        assert(coord1 != coord3)
    }

    @Test
    fun `Test plus operator`() {
        val coord1 = Coordinates(2, 3)
        val coord2 = Coordinates(4, 5)
        val result = coord1 + coord2
        assert(result == Coordinates(6, 8))
    }

    @Test
    fun `Test minus operator`() {
        val coord1 = Coordinates(5, 7)
        val coord2 = Coordinates(2, 3)
        val result = coord1 - coord2
        assert(result == Coordinates(3, 4))
    }

    @Test
    fun `Test isValid method`() {
        val boardSide = 8
        val validCoord = Coordinates(4, 5)
        val invalidCoord1 = Coordinates(0, 5)
        val invalidCoord2 = Coordinates(4, 9)
        val invalidCoord3 = Coordinates(9, 1)

        assert(validCoord.isValid(boardSide))
        assert(!invalidCoord1.isValid(boardSide))
        assert(!invalidCoord2.isValid(boardSide))
        assert(!invalidCoord3.isValid(boardSide))
    }
}