package pt.isel.reversi.core.storage.serializers

import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.PieceType
import kotlin.test.Test

class PlayerSerializerTest {
    val testUnit = SerializerTestUnit(PlayerSerializer()) {
        listOf(
            Player(PieceType.BLACK, 10),
            Player(PieceType.WHITE, 20),
            Player(PieceType.BLACK, 0),
            Player(PieceType.WHITE, 100)
        )
    }

    @Test
    fun `Test serialize and deserialize`() {
        testUnit.runTest()
    }
}