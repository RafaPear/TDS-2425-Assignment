package pt.isel.reversi.app.pages.lobby

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pt.isel.reversi.app.*
import pt.isel.reversi.app.exceptions.GameCorrupted
import pt.isel.reversi.app.exceptions.GameIsFull
import pt.isel.reversi.app.pages.lobby.showGames.ShowGames
import pt.isel.reversi.app.state.*
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.getAllGameNames
import pt.isel.reversi.core.loadGame
import pt.isel.reversi.core.readGame
import pt.isel.reversi.utils.LOGGER

enum class LobbyState {
    LOADING, EMPTY, SHOW_GAMES
}

private const val UI_DELAY_SHORT_MS = 100L
private const val POLL_INTERVAL_MS = 1000L
private const val PAGE_TRANSITION_DURATION_MS = 500


data class LobbyUiState(
    val games: List<Game> = emptyList(),
    val lobbyState: LobbyState = LobbyState.LOADING,
    val canRefresh: Boolean = false,
    val error: Exception? = null,
)

class LobbyViewModel : ViewModel() {
    private val _uiState = mutableStateOf(LobbyUiState())
    val uiState: State<LobbyUiState> = _uiState

    private var knownNames: List<String> = emptyList()

    init {
        refresh()
        startPolling()
    }

    fun refresh() {
        viewModelScope.launch {
            loadGamesAndUpdateState()
        }
    }

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
            knownNames = loaded.mapNotNull { it.currGameName }
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

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive) {
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
        }
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
}


@Composable
fun LobbyMenu(
    appState: MutableState<AppState>,
    viewModel: LobbyViewModel = viewModel()
) {
    val uiState by remember { viewModel.uiState }
    val games = uiState.games
    val lobbyState = uiState.lobbyState
    val canRefresh = uiState.canRefresh
    val error = uiState.error

    var selectedGame by remember { mutableStateOf<Game?>(null) }

    val refreshAction: @Composable () -> Unit = {
        if (canRefresh)
            RefreshButton {
                viewModel.refresh()
            }
    }

    //Transmite o error do viewModel para o appState, necessário porque o viewModel não conhece o View (appState)
    LaunchedEffect(error) {
        if (error == null) return@LaunchedEffect
        appState.setError(error)
    }

    ScaffoldView(appState, title = "Lobby - Jogos Guardados") { padding ->
        AnimatedContent(
            targetState = lobbyState,
            transitionSpec = {
                val iOSEasing = CubicBezierEasing(0.22f, 1f, 0.36f, 1f)
                reversiFadeAnimation(PAGE_TRANSITION_DURATION_MS, iOSEasing)
            },
            modifier = Modifier
                .fillMaxSize()
                .background(MAIN_BACKGROUND_COLOR),
            label = "PageTransition"
        ) { page ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = padding)
                    .padding(all = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                when (page) {
                    LobbyState.LOADING -> Loading()
                    LobbyState.EMPTY -> Empty { refreshAction() }
                    LobbyState.SHOW_GAMES -> ShowGames(
                        currentGameName = appState.value.game.currGameName,
                        games = games,
                        buttonRefresh = { refreshAction() }
                    ) { game ->
                        selectedGame = game
                    }
                }
            }

            selectedGame?.let { game ->
                LobbyLoadGame(
                    appState = appState,
                    game = game,
                    viewModel = viewModel,
                    onClose = { selectedGame = null }
                )
            }
        }
    }
}

@Composable
private fun LobbyLoadGame(
    appState: MutableState<AppState>,
    game: Game,
    viewModel: LobbyViewModel,
    onClose: () -> Unit
) {
    LOGGER.info("Jogo selecionado: ${game.currGameName}")

    val state = game.gameState
    val name = game.currGameName

    when {
        name == appState.value.game.currGameName -> {
            appState.setPage(Page.GAME)
            appState.value = appState.value.copy(backPage = Page.LOBBY)
            onClose()
            return
        }

        state == null -> {
            LOGGER.warning("Estado do jogo nulo para o jogo: ${game.currGameName}")
            appState.setError(
                GameCorrupted(
                    message = "O jogo '${game.currGameName}' está corrompido.",
                    type = ErrorType.ERROR
                )
            )
            onClose()
            return
        }

        state.players.isEmpty() -> {
            LOGGER.warning("Jogo cheio selecionado: ${game.currGameName}")
            appState.setError(GameIsFull())
            onClose()
            return
        }

        name == null -> {
            LOGGER.warning("Nome do jogo nulo ao tentar entrar no jogo.")
            appState.setError(
                GameCorrupted(
                    message = "O jogo selecionado não tem um nome válido.",
                    type = ErrorType.ERROR
                )
            )
            onClose()
            return
        }
    }

    val players = state.players.map { it.type }
    val scope = rememberCoroutineScope()

    PickAPiece(
        pieces = players,
        onPick = { pieceType ->
            val appGame = appState.value.game

            scope.launch {
                try {
                    appState.setLoading(true)

                    try {
                        appGame.saveEndGame()
                    } catch (e: Exception) {
                        LOGGER.warning("Erro ao salvar estado atual do jogo: ${e.message}")
                    }

                    val joinedGame = viewModel.tryLoadGame(gameName = name, desiredType = pieceType) ?: run {
                        onClose()
                        return@launch
                    }

                    LOGGER.info("Entrou no jogo '${joinedGame.currGameName}' como peça $pieceType.")

                    appState.getStateAudioPool().play(HIT_SOUND)
                    appState.setAppState(joinedGame, Page.GAME, backPage = Page.LOBBY)
                    onClose()
                } finally {
                    appState.setLoading(false)
                }
            }
        },
        onDismiss = {
            appState.setPage(Page.LOBBY)
            onClose()
        }
    )
}

/**
 * Modal dialog for picking a piece.
 * Clicking outside the inner dialog dismisses; clicking inside does not.
 */
@Composable
private fun PickAPiece(
    pieces: List<PieceType>,
    onPick: (PieceType) -> Unit,
    onDismiss: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            // detect taps on the background to dismiss; interior box handles its own clicks
            .pointerInput(Unit) {
                // detectTapGestures usage — onTap on background => dismiss
                detectTapGestures { onDismiss() }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                // interior should intercept clicks so they don't bubble to the background pointerInput
                .pointerInput(Unit) {
                    // consume all taps inside the dialog (so interior clicks don't dismiss)
                    detectTapGestures(onTap = { /* consumed */ })
                }
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Escolha a sua peça",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    pieces.forEach { piece ->
                        val color = when (piece) {
                            PieceType.BLACK -> Color.Black
                            PieceType.WHITE -> Color.White
                        }
                        val borderColor = when (piece) {
                            PieceType.BLACK -> Color.White.copy(alpha = 0.3f)
                            PieceType.WHITE -> Color.Black.copy(alpha = 0.2f)
                        }
                        // use IconButton as a tappable circular surface; content intentionally empty
                        IconButton(
                            onClick = { onPick(piece) },
                            modifier = Modifier
                                .size(80.dp)
                                .background(color, CircleShape)
                                .border(2.dp, borderColor, CircleShape)
                        ) { /* no icon inside, just the colored circle */ }
                    }
                }
            }
        }
    }
}

fun testTagLobbyBoard() = "LobbyBoardPreview"
fun testTagCellPreview(coordinateIndex: Int) = "LobbyCellPreview_$coordinateIndex"
fun testTagCarouselItem(name: String) = "LobbyCarouselItem_$name"
