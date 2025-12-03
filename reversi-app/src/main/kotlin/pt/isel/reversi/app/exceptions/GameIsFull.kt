package pt.isel.reversi.app.exceptions

import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.ReversiException

class GameIsFull(
    message: String = "The game is full",
    type: ErrorType = ErrorType.INFO,
): ReversiException(message, type)