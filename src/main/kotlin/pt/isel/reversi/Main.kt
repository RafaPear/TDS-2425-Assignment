package pt.isel.reversi

import pt.isel.reversi.board.Board
import pt.isel.reversi.commands.ExitCmd
import pt.isel.reversi.commands.NewCmd
import pt.rafap.ktflag.CommandParser
import pt.rafap.ktflag.style.Colors
import pt.rafap.ktflag.style.Colors.colorText

fun main(){
    var board = Board(8)

    val parser = CommandParser(NewCmd, ExitCmd)

    while (true){
        val result = parser.readInputAndGetResult(board)

        when {
            result == null -> println(colorText("[ERROR] Unknown command", Colors.RED))
            result.isError -> result.printError()
            result.result != null -> board = result.result!!
        }
    }
}