package pt.isel.reversi.app.pages.lobby

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.getAllGameNames
import pt.isel.reversi.core.loadGame
import pt.isel.reversi.core.readGame
import pt.isel.reversi.utils.LOGGER

private const val UI_DELAY_SHORT_MS = 100L
private const val POLL_INTERVAL_MS = 1000L

data class LobbyUiState(
    val games: List<Game> = emptyList(),
    val lobbyState: LobbyState = LobbyState.LOADING,
    val canRefresh: Boolean = false,
    val error: Exception? = null,
)

class LobbyViewModel {
    private val _uiState = mutableStateOf(LobbyUiState())
    val uiState: State<LobbyUiState> = _uiState

    private var knownNames: List<String> = emptyList()

    suspend fun refresh() = loadGamesAndUpdateState()


    private suspend fun loadGamesAndUpdateState() {
        _uiState.value = _uiState.value.copy(lobbyState = LobbyState.LOADING)
        try {
            val ids = getAllGameNames()
            delay(UI_DELAY_SHORT_MS)
            val loaded = ids.mapNotNull { id ->
                try {
                    readGame(id)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    LOGGER.warning("Erro ao ler jogo: $id - ${e.message}")
                    null
                }
            }
            knownNames = ids
            val newLobbyState = if (loaded.isEmpty()) LobbyState.EMPTY else LobbyState.SHOW_GAMES
            _uiState.value = _uiState.value.copy(
                games = loaded,
                lobbyState = newLobbyState,
                canRefresh = false,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LOGGER.warning("Erro ao carregar jogos: ${e.message}")

            _uiState.value = _uiState.value.copy(
                games = emptyList(),
                lobbyState = LobbyState.EMPTY,
                canRefresh = false,
                error = e
            )
        }
    }

    suspend fun startPolling() {
        try {
            val ids = getAllGameNames()
            if (ids != knownNames && ids.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(canRefresh = true)
                knownNames = ids
            } else if (ids.isEmpty() && knownNames.isNotEmpty()) {
                _uiState.value = _uiState.value.copy(canRefresh = true)
                knownNames = ids
            }
        } catch (e: Exception) {
            LOGGER.warning("Polling error: ${e.message}")
        }
        delay(POLL_INTERVAL_MS)
    }

    suspend fun tryLoadGame(gameName: String, desiredType: PieceType): Game? {
        return try {
            loadGame(gameName = gameName, desiredType = desiredType)
        } catch (e: Exception) {
            LOGGER.warning("Erro ao carregar jogo $gameName: ${e.message}")
            _uiState.value = _uiState.value.copy(error = e)
            null
        }
    }

    suspend fun refreshGame(game: Game) {
        try {
            val newGame = game.hardRefresh()
            if (newGame != game) _uiState.value = _uiState.value.copy(
                games = _uiState.value.games.map {
                    if (it.currGameName == newGame.currGameName) newGame
                    else it
                }
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            LOGGER.warning("Erro: ${e.message}, ${e.localizedMessage}")
        }

    }
}
