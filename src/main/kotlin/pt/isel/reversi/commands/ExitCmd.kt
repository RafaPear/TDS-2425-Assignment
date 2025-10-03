package pt.isel.reversi.commands

import pt.isel.reversi.board.Board
import pt.rafap.ktflag.cmd.CommandImpl
import pt.rafap.ktflag.cmd.CommandInfo
import pt.rafap.ktflag.cmd.CommandResult
import kotlin.system.exitProcess

object ExitCmd: CommandImpl<Board>() {
    override val info = CommandInfo(
        title = "Exit",
        description = "Exits the application.",
        aliases = listOf("exit", "quit", "q"),
        usage = "exit",
        minArgs = 0,
        maxArgs = 0
    )

    override fun execute(vararg arg: String, context: Board?): CommandResult<Board> {
        println("By byyyy")
        exitProcess(0)
    }
}