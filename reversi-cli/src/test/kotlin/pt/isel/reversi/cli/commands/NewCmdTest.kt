package pt.isel.reversi.cli.commands

import pt.rafap.ktflag.cmd.CommandResultType
import kotlin.test.Test

class NewCmdTest {

    @Test
    fun `Test NewCmd simple execution`() {
        val args = "#".split(" ").toTypedArray()
        val result = pt.isel.reversi.cli.commands.NewCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test NewCmd with name execution`() {
        val args = "# lalala".split(" ").toTypedArray()
        val result = pt.isel.reversi.cli.commands.NewCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.SUCCESS) {
            "Expected SUCCESS but got ${result.type} with message: ${result.message}"
        }
    }

    @Test
    fun `Test NewCmd fails execution by arguments`() {
        val args = emptyArray<String>()
        val result = NewCmd.executeWrapper(*args, context = null)
        assert(result.type == CommandResultType.INVALID_ARGS) {
            "Expected INVALID_ARGS but got ${result.type} with message: ${result.message}"
        }
    }
}