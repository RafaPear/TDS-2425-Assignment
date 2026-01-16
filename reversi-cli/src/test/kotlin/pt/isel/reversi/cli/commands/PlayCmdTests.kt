package pt.isel.reversi.cli.commands

import kotlinx.coroutines.test.runTest
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.gameServices.EmptyGameService
import pt.isel.reversi.core.startNewGame
import pt.isel.reversi.core.storage.MatchPlayers
import pt.rafap.ktflag.cmd.CommandResultType
import java.io.File
import kotlin.test.*

class PlayCmdTests {

    @BeforeTest
    fun cleanup() {
        File("data/saves").deleteRecursively()
    }

    @AfterTest
    fun cleanupAfter() {
        File("data/saves").deleteRecursively()
    }

    @Test
    fun `parseCoordinateArgs accepts separated numbers`() {
        val c = PlayCmd.parseCoordinateArgs(listOf("3", "5"))
        assertEquals(3, c?.row)
        assertEquals(5, c?.col)
    }

    @Test
    fun `parseCoordinateArgs accepts combined numeric and letter (1A)`() {
        val c = PlayCmd.parseCoordinateArgs(listOf("1A"))
        assertEquals(1, c?.row)
        // 'A' -> col 1
        assertEquals(1, c?.col)
    }

    @Test
    fun `parseCoordinateArgs accepts combined digits (15)`() {
        val c = PlayCmd.parseCoordinateArgs(listOf("15"))
        assertEquals(1, c?.row)
        assertEquals(5, c?.col)
    }

    @Test
    fun `parseCoordinateArgs returns null for invalid inputs`() {
        assertNull(PlayCmd.parseCoordinateArgs(listOf("x")))
        assertNull(PlayCmd.parseCoordinateArgs(listOf("")))
        assertNull(PlayCmd.parseCoordinateArgs(listOf("1")))
    }

    @Test
    fun `Test PlayCmd execution`() = runTest {
        val result = PlayCmd.executeWrapper(
            "3", "4",
            context = startNewGame(
                side = 8,
                players = MatchPlayers(Player(PieceType.BLACK), Player(PieceType.WHITE)),
                firstTurn = PieceType.BLACK,
                service = EmptyGameService()
            )
        )
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test PlayCmd execution fails on null game`() {
        val result = PlayCmd.executeWrapper("1", "4", context = null)
        assert(result.type == CommandResultType.ERROR) {
            "Expected ERROR but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test PlayCmd fails execution by arguments`() {
        val result = PlayCmd.executeWrapper(context = null)
        assert(result.type == CommandResultType.INVALID_ARGS) {
            "Expected INVALID_ARGS but got ${result.type} with message: ${result.message}"
        }
    }
}