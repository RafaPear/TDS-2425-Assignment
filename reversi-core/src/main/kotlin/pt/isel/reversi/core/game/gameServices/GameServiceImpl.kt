package pt.isel.reversi.core.game.gameServices

import pt.isel.reversi.core.game.Game
import pt.isel.reversi.core.gameState.GameState

interface GameServiceImpl {
    fun getStorageTypeName(): String
    suspend fun hasAllPlayers(game: Game): Boolean
    suspend fun refresh(game: Game): Game
    suspend fun refreshBase(game: Game): GameState?
    suspend fun hardLoad(id: String): GameState?
    suspend fun hardSave(id: String, gameState: GameState)
    suspend fun saveEndGame(game: Game)
    suspend fun saveOnlyBoard(gameName: String?, gameState: GameState?)
    suspend fun runStorageHealthCheck()
    suspend fun new(gameName: String, gameStateProvider: () -> GameState)
    suspend fun closeService()
    suspend fun getAllGameNames(): List<String>
}