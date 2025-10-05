package pt.isel.reversi.core.game

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType

/**
 * Interface for data access operations in Reversi games.
 */
interface GameDataAccessImpl {
    // POST METHODS
    /**
     * Stores a piece for a given player name.
     * @param piece The piece to store.
     * @param name The player's name.
     */
    fun postPiece(piece: Piece, name: String)
    /**
     * Stores a game state for a given name.
     * @param game The game state to store.
     * @param name The game name.
     */
    fun postGame(game: GameImpl, name: String)
    /**
     * Initializes a new game with the given side and name.
     * @param side The board size.
     * @param name The game name.
     */
    fun postInitGame(side: Int, name: String)
    /**
     * Stores available pieces for a player name.
     * @param piece The list of available piece types.
     * @param name The player's name.
     */
    fun postAvailablePiece(piece: List<PieceType>, name: String)

    // GET METHODS
    /**
     * Retrieves the board for a given game name.
     * @param name The game name.
     * @return The board.
     */
    fun getBoard(name: String): Board
    /**
     * Retrieves available pieces for a player name.
     * @param name The player's name.
     * @return List of available piece types.
     */
    fun getAvailablePieces(name: String): List<PieceType>
    /**
     * Retrieves the latest piece for a player name.
     * @param name The player's name.
     * @return The latest piece, or null if none.
     */
    fun getLatestPiece(name: String): Piece?

    // TODO ESTRUTURA FICHEIRO PARA IMPL COM FILES

    // availablePieces: @|#
    // side: n
    // piece: row col type
    // piece: row col type
    // piece: row col type
    // pass: type
}