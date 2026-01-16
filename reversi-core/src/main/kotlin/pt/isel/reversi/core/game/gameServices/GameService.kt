package pt.isel.reversi.core.game.gameServices

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.exceptions.BadStorage
import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.InvalidFile
import pt.isel.reversi.core.exceptions.InvalidGame
import pt.isel.reversi.core.game.Game
import pt.isel.reversi.core.gameState.GameState
import pt.isel.reversi.core.gameState.MatchPlayers
import pt.isel.reversi.core.gameState.Player
import pt.isel.reversi.core.loadCoreConfig
import pt.isel.reversi.core.storage.GameStorageType
import pt.isel.reversi.core.storage.StorageParams
import pt.isel.reversi.storage.AsyncStorage
import pt.isel.reversi.utils.LOGGER
import pt.isel.reversi.utils.TRACKER

/**
 * Primary implementation of the game service for managing game state persistence.
 * Provides integration with various storage backends (file system, MongoDB) and handles
 * game state synchronization, player management, and storage operations.
 *
 * @property storage The storage type to use (FILE_STORAGE or DATABASE_STORAGE).
 * @property params Configuration parameters for the selected storage backend.
 */
class GameService(storage: GameStorageType? = null, params: StorageParams? = null) : GameServiceImpl {
    private val storage: AsyncStorage<String, GameState, String> by lazy {
        params?.let { storage?.storage(it) } ?: GameStorageType.setUpStorage(loadCoreConfig())
    }

    override fun getStorageTypeName(): String = storage.javaClass.simpleName

    override suspend fun hasAllPlayers(game: Game): Boolean {
        val gs = game.requireStartedGame()
        val name = game.currGameName ?: return (gs.players.isFull())

        val loaded = storage.load(name) ?: throw InvalidFile(
            message = "Failed to load game state from storage: $name", type = ErrorType.WARNING
        )
        return (loaded.players.isFull())
    }

    override suspend fun refresh(game: Game): Game {
        TRACKER.trackFunctionCall(customName = "Game.refresh", category = "Core.Game")
        val gs = game.requireStartedGame()
        if (game.currGameName == null) return game

        val newLastModified = storage.lastModified(game.currGameName)

        if (newLastModified == game.lastModified) return game

        val loadedState = refreshBase(game) ?: return game
        val countPassCondition = loadedState.board == gs.board && loadedState.lastPlayer != gs.lastPlayer

        return game.copy(
            gameState = loadedState.refreshPlayers(),
            countPass = if (countPassCondition) game.countPass + 1 else 0,
            lastModified = newLastModified
        )
    }

    override suspend fun refreshBase(game: Game): GameState? {
        if (game.currGameName == null) return null

        val lastModified = storage.lastModified(game.currGameName)

        if (lastModified == game.lastModified) return null

        return storage.load(game.currGameName) ?: throw InvalidFile(
            message = "Failed to load game state from storage: ${game.currGameName}", type = ErrorType.WARNING
        )
    }

    override suspend fun hardLoad(id: String) = storage.load(id)

    override suspend fun hardSave(id: String, gameState: GameState) =
        storage.save(id, gameState)

    override suspend fun saveEndGame(game: Game) {
        TRACKER.trackFunctionCall(customName = "Game.saveEndGame", category = "Core.Game")
        val gs = game.requireStartedGame()

        val name = game.currGameName ?: throw InvalidFile(
            message = "Name of the current game is null", type = ErrorType.WARNING
        )

        storage.lastModified(game.currGameName) ?: run {
            storage.new(
                id = name,
            ) { gs.copy(players = MatchPlayers()) }
            return
        }

        val loadedGs = try {
            storage.load(game.currGameName)
        } catch (e: InvalidFile) {
            storage.delete(game.currGameName)
            LOGGER.warning("Deleted corrupted game from storage: ${game.currGameName} due to ${e.message}")
            return
        }


        var playersInStorage = loadedGs?.players ?: MatchPlayers()

        if (loadedGs != null && loadedGs.winner != null && loadedGs.winner == gs.winner) {
            LOGGER.info("Game already ended in storage: ${game.currGameName}")
            storage.delete(game.currGameName)
            LOGGER.info("Deleted ended game from storage: ${game.currGameName}")
            return
        }

        val myPieceTemp = game.myPiece ?: throw InvalidGame(
            message = "Game is not started yet.", type = ErrorType.WARNING
        )

        playersInStorage = MatchPlayers(null, playersInStorage.getPlayerByType(myPieceTemp.swap()))

        LOGGER.info("Saving game state to storage: ${game.currGameName}")
        storage.save(
            id = game.currGameName, obj = gs.copy(
                players = playersInStorage,
            )
        )
    }

    override suspend fun saveOnlyBoard(gameName: String?, gameState: GameState?) {
        val gs = gameState ?: throw InvalidGame(
            message = "Game is not started yet.", type = ErrorType.WARNING
        )

        val name = gameName ?: throw InvalidFile(
            message = "Name of the current game is null", type = ErrorType.WARNING
        )

        storage.lastModified(id = name) ?: run {
            try {
                storage.new(id = name) { gameState }
                return@saveOnlyBoard
            } catch (e: Exception) {
                throw InvalidFile(
                    message = e.message.toString(), type = ErrorType.CRITICAL
                )
            }
        }

        val ls = storage.load(id = name) ?: throw InvalidFile(
            message = "Failed to load game state from storage: $name", type = ErrorType.ERROR
        )


        var lsGameState = ls

        ls.players.forEachIndexed { index, player ->
            val gsPlayer = gs.players[index]
            if (gsPlayer != null && gsPlayer.name != player.name) {
                lsGameState = lsGameState.changeName(newName = gsPlayer.name, pieceType = gsPlayer.type)
            }
        }

        storage.save(
            id = name, obj = gs.copy(
                players = lsGameState.players,
            )
        )
    }

    override suspend fun new(gameName: String, gameStateProvider: () -> GameState) {
        TRACKER.trackFunctionCall(customName = "GameService.new", category = "Core.GameService")
        storage.new(
            id = gameName, factory = gameStateProvider
        )
    }

    override suspend fun delete(gameName: String) {
        TRACKER.trackFunctionCall(customName = "GameService.delete", category = "Core.GameService")
        storage.delete(gameName)
    }

    override suspend fun runStorageHealthCheck() {
        val testId = "health_check_test_game"
        val testState = GameState(
            players = MatchPlayers(Player(PieceType.BLACK)),
            lastPlayer = PieceType.WHITE,
            board = Board(8),
            winner = null
        )
        if (storage.lastModified(testId) != null) storage.delete(testId)

        storage.new(testId) { testState }
        val loadedState = storage.load(testId)
        if (loadedState != testState) throw BadStorage(
            message = "Storage health check failed: loaded state does not match created state."
        )
        storage.save(testId, testState)
        val reloadedState = storage.load(testId)
        if (reloadedState != testState) throw BadStorage(
            message = "Storage health check failed: reloaded state does not match saved state."
        )
        storage.delete(testId)
        if (storage.load(testId) != null) throw BadStorage(
            message = "Storage health check failed: deleted state still exists in storage."
        )
    }

    /**
     * Retrieves all game names stored in the configured storage backend.
     * @return A list of all game identifiers in storage.
     */
    override suspend fun getAllGameNames(): List<String> {
        return storage.loadAllIds()
    }

    override suspend fun closeService() {
        storage.close()
    }
}