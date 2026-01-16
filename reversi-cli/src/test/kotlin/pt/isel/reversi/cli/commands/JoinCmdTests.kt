package pt.isel.reversi.cli.commands

import kotlinx.coroutines.test.runTest
import pt.rafap.ktflag.cmd.CommandResultType
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertFails

class JoinCmdTests {

    @BeforeTest
    fun cleanup() {
        File("data/saves").deleteRecursively()
    }

    @AfterTest
    fun cleanupAfter() {
        File("data/saves").deleteRecursively()
    }

    @Test
    fun `Test JoinCmd execution`() = runTest {
        val piece = "#"
        val name = "test_player"
        NewCmd.executeWrapper(piece, name, context = null)
        val result = JoinCmd.executeWrapper(name, context = null)
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }

    }

    @Test
    fun `Test JoinCmd execution fails on already full game`() {
        val piece = "#"
        val name = "test_player"

        NewCmd.executeWrapper(piece, name, context = null)
        JoinCmd.executeWrapper(name, context = null)

        assertFails("Expected an exception when joining a full game") {
            JoinCmd.executeWrapper(name, context = null)
        }

    }

    @Test
    fun `Test JoinCmd fails execution by arguments`() {
        val args = emptyArray<String>()
        val result = JoinCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.INVALID_ARGS) {
            "Expected INVALID_ARGS but got ${result.type} with message: ${result.message}"
        }
    }
}