package pt.isel.reversi.cli

import pt.isel.reversi.cli.commands.ExitCmd
import pt.isel.reversi.cli.commands.NewCmd
import pt.isel.reversi.core.board.Board
import pt.rafap.ktflag.CommandParser
import pt.rafap.ktflag.cmd.CommandResultType
import pt.rafap.ktflag.style.Colors
import pt.rafap.ktflag.style.Colors.colorText

/**
 * Entry point for the CLI version of the Reversi game.
 * Initializes the board and command parser, and handles user input.
 */
fun runCli() {
    /**
     * The current game board. Initialized with size 8.
     */
    var board = Board(8)

    val parser = CommandParser(NewCmd, ExitCmd)

    while (true) {
        val input = parser.readInput()
        val result = parser.parseInputToResult(input, board)

        if (result == null) {
            println(
                colorText(
                    "[ERROR] Unknown command",
                    Colors.RED
                )
            )
            continue
        }

        when {
            result.type == CommandResultType.ERROR           -> result.printError()
            result.type == CommandResultType.UNKNOWN_COMMAND -> parser.printUnknownCommandError(input, result)
            result.result != null                            -> board = result.result!!
        }
    }
}