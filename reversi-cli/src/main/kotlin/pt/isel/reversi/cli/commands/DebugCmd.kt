package pt.isel.reversi.cli.commands

import pt.isel.reversi.cli.CLI_CONFIG
import pt.isel.reversi.core.CORE_CONFIG
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.SAVES_FOLDER
import pt.isel.reversi.core.stringifyBoard
import pt.rafap.ktflag.cmd.CommandImpl
import pt.rafap.ktflag.cmd.CommandInfo
import pt.rafap.ktflag.cmd.CommandResult
import pt.rafap.ktflag.style.Colors
import pt.rafap.ktflag.style.Colors.colorText

/**
 * Command to debug the game state.
 */
object DebugCmd : CommandImpl<Game>() {
    override val info: CommandInfo = CommandInfo(
        title = "Debug",
        description = "Debug command for testing purposes.",
        longDescription = "A command used for debugging the application. This command is intended for development and testing purposes only.",
        aliases = listOf("dbg", "debug"),
        usage = "debug",
        minArgs = 0,
        maxArgs = 0
    )

    private fun title(text: String): String {
        val line = "-----------------------------------------"
        return "$line\n$text\n$line\n"
    }

    override fun execute(
        vararg args: String,
        context: Game?
    ): CommandResult<Game> {

        val target = context?.target
        val name = context?.currGameName
        val state = context?.gameState
        val count = context?.countPass

        val sb = StringBuilder()

        sb.appendLine(title("DEBUG COMMAND OUTPUT"))

        if (context != null) {
            sb.append(title("Game State"))
            if (state == null) {
                sb.appendLine(colorText("\tState: Not initialized", Colors.RED))
            } else {
                sb.appendLine(colorText("\tState: Initialized", Colors.GREEN))
                sb.appendLine("\tLast play: ${state.lastPlayer.symbol}")
                sb.appendLine("\tPlayers:")
                state.players.forEach { player ->
                    sb.appendLine("\t\t - Player ${player.type.symbol}: ${player.points} points")
                }

                sb.appendLine()

                val board = state.board
                sb.appendLine("\tBoard: ")
                sb.appendLine("\t\t - Side: ${board.side}")
                sb.appendLine("\t\t - Total of Pieces on board: ${board.count()}")
                sb.appendLine("\t\t - Board Representation:")
                sb.appendLine(context.stringifyBoard())

                sb.appendLine()

                sb.appendLine("\tTarget: $target")
                sb.appendLine("\tCurrent Game Name: $name")
                sb.appendLine("\tConsecutive Passes: $count")
            }
        }

        sb.appendLine(title("Core Configurations"))
        for ((key, value) in CORE_CONFIG.map)
            sb.appendLine("\t - $key: $value")
        sb.appendLine()

        sb.appendLine(title("CLI Configurations"))
        for ((key, value) in CLI_CONFIG.map)
            sb.appendLine("\t - $key: $value")
        sb.appendLine()

        sb.appendLine(title("Saves Folder Contents"))
        val files = java.io.File(SAVES_FOLDER).listFiles()
        if (files.isNullOrEmpty()) {
            sb.appendLine("\tNo saved games found.")
        } else {
            for (file in files) {
                if (file.name == name)
                    sb.appendLine(
                        colorText(
                            "\t - ${file.name} (${file.length()} bytes) <- (Current Game)",
                            Colors.GREEN
                        )
                    )
                else
                    sb.appendLine("\t - ${file.name} (${file.length()} bytes)")
            }
        }

        return CommandResult.SUCCESS(sb.toString(), context)
    }
}