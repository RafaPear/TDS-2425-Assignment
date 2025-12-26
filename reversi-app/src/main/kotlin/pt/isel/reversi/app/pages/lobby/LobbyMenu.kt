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
import pt.isel.reversi.utils.LOGGER

enum class LobbyState {
    LOADING, EMPTY, SHOW_GAMES
}

private const val PAGE_TRANSITION_DURATION_MS = 500

@Composable
fun LobbyMenu(
    appState: MutableState<AppState>,
    viewModel: LobbyViewModel = remember { LobbyViewModel() },
) {

    val uiState = viewModel.uiState.value
    val games = uiState.games
    val lobbyState = uiState.lobbyState
    val canRefresh = uiState.canRefresh
    val error = uiState.error

    var selectedGame by remember { mutableStateOf<Game?>(null) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        LOGGER.info("Starting polling for lobby updates.")
        try {
            viewModel.refresh()
            while (isActive) {
                viewModel.startPolling()
            }
        } finally {
            LOGGER.info("Lobby polling stopped.")
        }
    }

    val refreshAction: @Composable () -> Unit = {
        if (canRefresh) {
            RefreshButton {
                scope.launch {
                    viewModel.refresh()
                }
            }
        }
    }

    //TODO: transferir esta esta lógica de alteração do appState para o ViewModel quando o appState fizer parte do ViewModel
    // por enquanto está aqui porque o ViewModel não deve conhecer o AppState, porque é parte da View
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
                        viewModel,
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

    //TODO: transferir esta esta lógica de alteração do appState para o ViewModel quando o appState fizer parte do ViewModel
    // por enquanto está aqui porque o ViewModel não deve conhecer o AppState, porque é parte da View
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
            .pointerInput(Unit) {
                detectTapGestures { onDismiss() }
            }
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { })
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

                        IconButton(
                            onClick = { onPick(piece) },
                            modifier = Modifier
                                .size(80.dp)
                                .background(color, CircleShape)
                                .border(2.dp, borderColor, CircleShape)
                        ) { }
                    }
                }
            }
        }
    }
}

fun testTagLobbyBoard() = "LobbyBoardPreview"
fun testTagCellPreview(coordinateIndex: Int) = "LobbyCellPreview_$coordinateIndex"
fun testTagCarouselItem(name: String) = "LobbyCarouselItem_$name"
