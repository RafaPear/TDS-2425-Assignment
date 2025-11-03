package pt.isel.reversi.cli.commands

import pt.rafap.ktflag.cmd.CommandResultType
import kotlin.test.Test

class JoinCmdTest {

    @Test
    fun `Test JoinCmd execution`() {
        val args = "# lalala".split(" ").toTypedArray()
        val result = JoinCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test JoinCmd execution fails on already full game`() {
        val piece = "#"
        val name  = "test_player"

        NewCmd.executeWrapper(piece, name, context = null)

        val result = JoinCmd.executeWrapper(piece, name, context = null)
        assert(result.type == CommandResultType.ERROR) {
            "Expected ERROR but got ${result.type} with message: ${result.message}"
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