package pt.isel.reversi.app.exceptions

import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.ReversiException

class GameNotStartedYet(
    message: String = "The game has not started yet",
    type: ErrorType = ErrorType.WARNING,
) : ReversiException(message, type)