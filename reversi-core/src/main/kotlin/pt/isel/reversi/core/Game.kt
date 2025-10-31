package pt.isel.reversi.core

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.exceptions.InvalidGameException
import pt.isel.reversi.core.exceptions.InvalidPlayException
import pt.isel.reversi.core.storage.GameState
import pt.isel.reversi.storage.Storage

/**
 * Lightweight test/dummy implementation of [Game] used for data access and integration tests.
 *
 * Only acts as a structural carrier for required properties; behavioural methods are left as TODOs
 * so they surface if accidentally invoked in logic outside targeted tests. Use the nested helper
 * subclasses to build simple game states for tests (empty, one player or two players).
 *
 * Note: This class is intentionally minimal and not suitable for exercising game logic.
 */
data class Game(
    val dataAccess: Storage<String, GameState, String>,
    val players: List<Player>,
    val target: Boolean,
    val playerTurn: PieceType = First_Player_TURN,
    val currGameName: String?,
    val board: Board?,
    val countPass: Int = 0,
) {

    constructor() : this(
        dataAccess = FILE_DATA_ACCESS,
        players = emptyList(),
        target = false,
        playerTurn = First_Player_TURN,
        currGameName = null,
        board = null,
    )

    fun isStarted(): Boolean =
        board != null && players.isNotEmpty()

    /**
     * Plays a move at the specified coordinate.
     * Saves the piece to data access if the game is not local.
     * Only check player turn if it is a not local game.
     * @param coordinate The (row, column) coordinate for the move.
     * @return The new game state after the move.
     * @throws InvalidPlayException if it's not the player's turn or if the play is invalid.
     * @throws IllegalArgumentException if the position is out of bounds.
     * @throws InvalidGameException if the game is not started yet (board is null).
     */
    fun play(coordinate: Coordinate): Game {
        var newBoard = if (!isStarted()) throw InvalidGameException(
            "Game is not started yet (board is null or players are empty)."
        ) else board as Board

        //if it has only one player and is not his turn
        if (players.size == 1 && players[0].type != playerTurn) {
            throw InvalidPlayException("It's not your turn")
        }

        val piece = Piece(coordinate, playerTurn)

        newBoard = GameLogic().play(newBoard, myPiece = piece)

        val newPlayers = players.map { it.refresh(newBoard) }

        val nextPlayerTurn = playerTurn.swap()

        // save the piece to the data access if game is not local
        val tempCurrGameName = currGameName


        if (tempCurrGameName != null) {
            TODO("https://github.com/RafaPear/Reversi-Grupo1/issues/30")
            //dataAccess.postPiece(tempCurrGameName, piece)
            if (countPass != 0) {
                TODO("reset count pass in data access")
            }
        }

        return this.copy(
            board = newBoard,
            players = newPlayers,
            playerTurn = nextPlayerTurn,
            countPass = 0
        )
    }

    /**
     * Gets the available piece options in the data access for the current game.
     * if the game is local (currGameName is null), returns an empty list.
     * @return List of available piece types.
     * @throws java.io.IOException if there is an error accessing the data.
     */
    fun pieceOptions(): List<PieceType> {
        TODO("https://github.com/RafaPear/Reversi-Grupo1/issues/30")
        // val tempCurrGameName = currGameName
        // return when {
        //     tempCurrGameName != null -> dataAccess.getAvailablePieces(tempCurrGameName)
        //     players.size == 1        -> players.map { it.type.swap() }
        //     players.isEmpty()        -> PieceType.entries
        //     else                     -> emptyList()
        // }
    }

    /**
     * Sets the target mode for the game.
     * @param target True to enable target mode.
     * @return The updated game state.
     */
    fun setTargetMode(target: Boolean): Game =
        this.copy(target = target)

    /** Gets the available plays for the current player.
     * If it is not a local game, and it is not the player's turn, returns an empty list.
     * @return List of available plays.
     * @throws InvalidGameException if the game is not started yet (board is null).
     */
    fun getAvailablePlays(): List<Coordinate> {
        if (!isStarted()) throw InvalidGameException(
            message = "Game is not started yet (board is null)."
        )

        // if it is not a local game, and it is not the player's turn
        if (players.size == 1 && players[0].type != playerTurn) {
            return emptyList()
        }

        return GameLogic().getAvailablePlays(
            board = board as Board,
            myPieceType = playerTurn
        )
    }

    /**
     * Starts a new game.
     * @param side The side length of the board.
     * @param players The list of players.
     * @param firstTurn The piece type of the player who goes first.
     * @param currGameName The current game name.
     * @return The new game state.
     */
    fun startNewGame(
        side: Int = BOARD_SIDE,
        players: List<Player> = emptyList(),
        firstTurn: PieceType = First_Player_TURN,
        currGameName: String? = null,
    ): Game {
        val board = Board(side).startPieces()
        return this.copy(
            board = board,
            players = players.map { it.refresh(board) },
            currGameName = currGameName,
            playerTurn = firstTurn
        )
    }

    fun pass(): Game {
        if (!isStarted()) throw InvalidGameException(
            message = "Game is not started yet (board is null or players are empty)."
        )

        if (players.size == 1 && players[0].type != playerTurn) {
            throw InvalidPlayException(
                message = "It's not your turn"
            )
        }

        val tempCurrGameName = currGameName

        if (tempCurrGameName != null) {
            TODO("https://github.com/RafaPear/Reversi-Grupo1/issues/30")
            //dataAccess.postPass(tempCurrGameName, playerTurn)
        }

        return this.copy(
            playerTurn = playerTurn.swap(),
            countPass = countPass + 1
        )
    }

    fun refresh(): Game {
        TODO("Not yet implemented")
    }

    fun poopBoard(): Board {
        if (currGameName == null) {
            return board ?: throw InvalidGameException(
                "Game is not started yet (board is null)."
            )
        }
        TODO("Not yet implemented when game is not local")
    }

    fun equals(other: Game): Boolean {
        return this.players == other.players &&
               this.target == other.target &&
               this.playerTurn == other.playerTurn &&
               this.currGameName == other.currGameName &&
               this.board == other.board &&
               this.countPass == other.countPass
    }
}
