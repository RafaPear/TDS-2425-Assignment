package pt.isel.reversi.cli.commands

import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.GameImpl
import pt.rafap.ktflag.cmd.CommandImpl
import pt.rafap.ktflag.cmd.CommandInfo
import pt.rafap.ktflag.cmd.CommandResult

object ShowCmd : CommandImpl<GameImpl>() {
    override val info: CommandInfo = CommandInfo(
        title = "Show",
        description = "Shows the current game state.",
        longDescription = "Displays the current board, player scores, and other relevant game information.",
        aliases = listOf("s", "show"),
        usage = "show",
        minArgs = 0,
        maxArgs = 0
    )

    override fun execute(
        vararg args: String,
        context: GameImpl?
    ): CommandResult<GameImpl> {
        if (context == null)
            return CommandResult.ERROR("Game is not defined. Cannot show game state.")

        val board = context.board
        val players = context.players

        if (board == null) {
            return CommandResult.ERROR("Board is not initialized.")
        }

        val builder = StringBuilder()
        builder.appendLine("Current Game State:")
        for (row in 0..board.side) {
            for (col in 0..board.side) {

                when {
                    row == 0 && col == 0 -> builder.append("  ")
                    row == 0             -> builder.append("$col ")
                    col == 0             -> builder.append("$row ")
                    else                 -> {
                        val cords = Coordinate(row, col)
                        val piece = board[cords]
                        val symbol = piece?.symbol ?: '.'

                        builder.append("$symbol ")
                    }
                }

            }
            builder.appendLine()
        }
        builder.appendLine("Player Scores:")
        players.forEach { player ->
            builder.appendLine("Player ${player.type.symbol}: ${player.points} points")
        }

        println(builder.toString())

        return CommandResult.SUCCESS(builder.toString(), context)
    }

}