package pt.isel.reversi.cli

import pt.isel.reversi.cli.commands.*
import pt.rafap.ktflag.cmd.args.CommandArg
import pt.rafap.ktflag.cmd.args.CommandArgsParser

private val debugArg = CommandArg(
    name = "Debug",
    description = "Enables debug mode with additional commands.",
    aliases = arrayOf("--debug", "-d"),
    isRequired = false,
    returnsValue = false,
)

fun main(args: Array<String>) {
    val parser = CommandArgsParser(
        debugArg
    )

    val parsed = parser.parseArgs(*args)

    val debug = parsed[debugArg] != null

    val cli = CLI(
        arrayOf(PlayCmd, PassCmd, NewCmd, JoinCmd, ExitCmd, ShowCmd),
        debug
    )
    cli.startLoop()
}