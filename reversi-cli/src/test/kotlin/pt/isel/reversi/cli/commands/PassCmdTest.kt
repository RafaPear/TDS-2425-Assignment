package pt.isel.reversi.cli.commands

import pt.isel.reversi.core.Game
import pt.rafap.ktflag.cmd.CommandResultType
import kotlin.test.Test

class PassCmdTest {

    @Test
    fun `Test PassCmd execution`() {
        val result = PassCmd.executeWrapper(
            context = Game().startNewGame()
        )
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test PassCmd execution fails on null game`() {
        val result = PassCmd.executeWrapper(context = null)
        assert(result.type == CommandResultType.ERROR) {
            "Expected ERROR but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test PassCmd fails execution by arguments`() {
        val args = arrayOf("extraArg")
        val result = PassCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.INVALID_ARGS) {
            "Expected INVALID_ARGS but got ${result.type} with message: ${result.message}"
        }
    }
}