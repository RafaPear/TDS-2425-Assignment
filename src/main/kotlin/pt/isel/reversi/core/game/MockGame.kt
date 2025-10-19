package pt.isel.reversi.core.game

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.PieceType

/**
 * Lightweight test/dummy implementation of [GameImpl] used for data access and integration tests.
 *
 * Only acts as a structural carrier for required properties; behavioural methods are left as TODOs
 * so they surface if accidentally invoked in logic outside targeted tests. Use the nested helper
 * subclasses to build simple game states for tests (empty, one player or two players).
 *
 * Note: This class is intentionally minimal and not suitable for exercising game logic.
 */
@Suppress("unused")
open class MockGame(
    override val dataAccess: GDAImpl,
    override val players: List<Player>,
    override val currGameName: String?,
    override val board: Board?,
    override val target: Boolean,
    override val isLocal: Boolean
) : GameImpl {
    override fun play(coordinate: Coordinate): GameImpl {
        TODO("Not yet implemented")
    }

    override fun pieceOptions(): List<PieceType> {
        TODO("Not yet implemented")
    }

    override fun setTargetMode(target: Boolean): GameImpl {
        TODO("Not yet implemented")
    }

    override fun getAvailablePlays(): List<Coordinate> {
        TODO("Not yet implemented")
    }

    override fun startNewGame() {
        TODO("Not yet implemented")
    }

    override fun pass() {
        TODO("Not yet implemented")
    }

    override fun refresh(): GameImpl {
        TODO("Not yet implemented")
    }

    override fun poopBoard(): Board {
        TODO("Not yet implemented")
    }

    /**
     * Convenience copy function to mutate selected fields for tests.
     */
    override fun copy(
        dataAccess: GDAImpl,
        players: List<Player>,
        currGameName: String?,
        board: Board?,
        target: Boolean,
        isLocal: Boolean
    ) = MockGame(dataAccess, players, currGameName, board, target, isLocal)

    /**
     * Helper representing a game with no players — useful to test initial write semantics.
     */
    class EmptyPlayers(dataAccess: GDAImpl, path: String) : MockGame(
        dataAccess = dataAccess,
        players = emptyList(),
        currGameName = path,
        board = Board(8),
        target = false,
        isLocal = false
    )

    /**
     * Helper representing a game with a single black player.
     */
    class OnePlayer(dataAccess: GDAImpl, path: String) : MockGame(
        dataAccess = dataAccess,
        players = listOf(Player(PieceType.BLACK, 0, 32)),
        currGameName = path,
        board = Board(8),
        target = false,
        isLocal = false
    )

    /**
     * Helper representing a game with two players (black and white).
     */
    class TwoPlayers(dataAccess: GDAImpl, path: String) : MockGame(
        dataAccess = dataAccess,
        players = listOf(Player(PieceType.BLACK, 0, 32), Player(PieceType.WHITE, 0, 32)),
        currGameName = path,
        board = Board(8),
        target = false,
        isLocal = false
    )
}