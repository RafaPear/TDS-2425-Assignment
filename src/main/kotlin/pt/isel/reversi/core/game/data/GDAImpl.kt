package pt.isel.reversi.core.game.data

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.game.GameImpl

/**
 * # Game Data Access Implementation (GDAImpl)
 * Contract for persistence operations related to a Reversi game.
 *
 *
 * Implementations are responsible for translating domain objects (board, pieces, passes) to a
 * storage representation and back, while returning structured [GDAResult] objects that
 * encode success/error semantics without throwing for expected domain conditions (e.g. not found).
 */
interface GDAImpl {
    // POST METHODS

    /** Persist a newly placed [piece] into the game identified by [fileName]. */
    fun postPiece(fileName: String, piece: Piece): GDAResult<Boolean>

    /** Persist initial game state (players, board size, etc.) under [fileName]. */
    fun postGame(fileName: String, game: GameImpl): GDAResult<Boolean>

    /** Record a pass action for the player of [pieceType] in the game file. */
    fun postPass(fileName: String, pieceType: PieceType): GDAResult<Boolean>

    // GET METHODS

    /** Load the current board reconstruction from persisted piece history under [fileName]. */
    fun getBoard(fileName: String): GDAResult<Board>

    /** Retrieve the remaining/declared available piece types from the header of [fileName]. */
    fun getAvailablePieces(fileName: String): GDAResult<List<PieceType>>

    /** Obtain the last placed piece (chronologically) recorded in [fileName], if any. */
    fun getLatestPiece(fileName: String): GDAResult<Piece?>

    // TODO ESTRUTURA FICHEIRO PARA IMPL COM FILES

    // availablePieces: @|#
    // side: n
    // piece: row col type
    // piece: row col type
    // piece: row col type
    // pass: type
}