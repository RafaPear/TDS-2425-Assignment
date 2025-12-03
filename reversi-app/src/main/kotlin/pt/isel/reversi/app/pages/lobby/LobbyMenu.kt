package pt.isel.reversi.app.pages.lobby

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pt.isel.reversi.app.ScaffoldView
import pt.isel.reversi.app.exceptions.GameIsFull
import pt.isel.reversi.app.state.*
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.loadGame
import pt.isel.reversi.core.readGame
import pt.isel.reversi.utils.LOGGER
import kotlin.math.absoluteValue


private val PRIMARY = Color(0xFF1976D2)
private val BACKGROUND = Color(0xFF121212)

enum class GameStatus(val text: String, val color: Color) {
    EMPTY("Vazio", Color.Green),
    WAITING_FOR_PLAYERS("Aguardando Jogadores", Color.Yellow),
    FULL("Cheio", Color.Blue),
    CORRUPTED("Corrompido", Color.Red),
    CURRENT_GAME("Jogo Atual", Color.Cyan)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LobbyCarousel(appState: MutableState<AppState>, games: List<Game>, onGameClick: (Game) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { games.size })
    val scope = rememberCoroutineScope()

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val availableWidth = this.maxWidth
        val availableHeight = this.maxHeight

        // 1. DEFINIR TAMANHOS MÁXIMOS DO CARTÃO PRIMEIRO (CORREÇÃO)
        val maxCardWidth = (availableWidth * 0.7f).coerceAtMost(450.dp)
        val maxCardHeight = (availableHeight * 0.8f).coerceAtMost(950.dp)

        val horizontalPadding = (availableWidth / 2 - maxCardWidth / 2)

        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = horizontalPadding), pageSpacing = 16.dp
        ) { page ->
            val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
            val scale = 0.85f + (1f - pageOffset.absoluteValue.coerceIn(0f, 1f)) * 0.15f

            Box(
                modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                val game = remember(games[page]) { mutableStateOf(games[page]) }
                val statusData = remember(game.value) { mutableStateOf(GameStatus.CORRUPTED) }
                val scope = rememberCoroutineScope()

                scope.launch {
                    while (isActive) {
                        try {
                            game.value = game.value.hardRefresh()
                            val state = game.value.gameState ?: throw Exception("Estado do jogo nulo")
                            statusData.value = when {
                                appState.value.game.currGameName == game.value.currGameName -> GameStatus.CURRENT_GAME
                                state.players.size == 2 -> GameStatus.EMPTY
                                state.players.size == 1 -> GameStatus.WAITING_FOR_PLAYERS
                                state.players.isEmpty() -> GameStatus.FULL
                                else -> GameStatus.CORRUPTED
                            }
                        } catch (e: Exception) {
                            statusData.value = GameStatus.CORRUPTED
                            LOGGER.warning("Erro ao atualizar jogo: ${game.value.currGameName} - ${e.message}")
                        }
                        val delay = when (statusData.value) {
                            GameStatus.EMPTY -> 100L
                            GameStatus.WAITING_FOR_PLAYERS -> 500L
                            GameStatus.FULL -> 15000L
                            GameStatus.CORRUPTED -> 20000L
                            GameStatus.CURRENT_GAME -> 100L
                        }
                        delay(delay)
                    }
                }

                GameCard(
                    game = game.value,
                    statusData = statusData.value,
                    scale = scale,
                    maxWidth = maxCardWidth,
                    maxHeight = maxCardHeight,
                    enabled = statusData.value !in listOf(GameStatus.CORRUPTED),
                    onClick = {
                        if (page == pagerState.currentPage) {
                            onGameClick(games[page])
                        } else {
                            scope.launch { pagerState.animateScrollToPage(page) }
                        }
                    })
            }
        }

        if (games.size > 1) {
            if (pagerState.currentPage > 0) {
                NavButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBackIos,
                    alignment = Alignment.CenterStart,
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } })
            }
            if (pagerState.currentPage < games.size - 1) {
                NavButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    alignment = Alignment.CenterEnd,
                    onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } })
            }
        }


        Column(
            Modifier.align(Alignment.TopCenter).padding(top = 24.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PageIndicators(games.size, pagerState.currentPage)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "${pagerState.currentPage + 1} de ${games.size}",
                fontSize = 14.sp,
                color = Color.White.copy(0.6f)
            )
        }
    }
}

@Composable
fun BoxScope.NavButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector, alignment: Alignment, onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.align(alignment).padding(16.dp).size(50.dp)
            .background(Color(0xFF2D2D2D).copy(0.9f), CircleShape).border(1.dp, Color.White.copy(0.2f), CircleShape)
    ) {
        val icons = listOf(
            Icons.AutoMirrored.Rounded.ArrowBackIos, Icons.AutoMirrored.Rounded.ArrowForwardIos
        )
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White,
            modifier = if (icon in icons) Modifier.padding(start = 4.dp) else Modifier
        )
    }
}

@Composable
fun PageIndicators(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            val width by animateDpAsState(
                if (index == current) 28.dp else 8.dp, spring(Spring.DampingRatioMediumBouncy), label = "indicator"
            )
            Box(
                Modifier.width(width).height(8.dp).clip(RoundedCornerShape(4.dp))
                    .background(Color.White.copy(if (index == current) 1f else 0.3f))
            )
        }
    }
}

// =============================================================================
// Menu Principal
// =============================================================================
@Composable
fun LobbyMenu(appState: MutableState<AppState>, modifier: Modifier = Modifier) {
    val games = remember { mutableStateListOf<Game>() }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        isLoading = true
        val ids = appState.value.game.getAllSavedGames()
        val loaded = ids.mapNotNull { id ->
            try {
                readGame(id)
            } catch (e: Exception) {
                LOGGER.warning("Erro ao ler jogo: $id - ${e.message}")
                null
            }
        }
        games.clear()
        games.addAll(loaded)
        isLoading = false
    }

    ScaffoldView(appState, title = "Lobby - Jogos Guardados") { padding ->
        val selectedGame = remember { mutableStateOf<Game?>(null) }
        Box(
            Modifier.fillMaxSize().background(BACKGROUND).padding(padding)
        ) {
            when {
                isLoading -> Loading()
                games.isEmpty() -> Empty()
                else -> LobbyCarousel(appState, games) { game ->
                    LOGGER.info("Jogo selecionado: ${game.currGameName}")
                    val state = game.gameState
                    if (state == null) {
                        LOGGER.warning("Estado do jogo nulo para o jogo: ${game.currGameName}")
                        return@LobbyCarousel
                    }
                    if (state.players.isEmpty()) {
                        LOGGER.warning("Jogo cheio selecionado: ${game.currGameName}")
                        appState.value = setError(appState, error = GameIsFull())
                        return@LobbyCarousel
                    }

                    val name = game.currGameName
                    if (name == null) {
                        LOGGER.warning("Nome do jogo nulo ao tentar entrar no jogo.")
                        return@LobbyCarousel
                    }
                    val appGame = appState.value.game
                    scope.launch {
                        try {
                            appGame.saveEndGame()
                        } catch (e: Exception) {
                            LOGGER.warning("Erro ao salvar estado atual do jogo: ${e.message}")
                        }
                    }

                    if (appGame.currGameName == name) {
                        appState.value = setGame(appState, appGame)
                        appState.value = setPage(appState, Page.GAME)
                        appState.value = appState.value.copy(backPage = Page.LOBBY)
                        return@LobbyCarousel
                    }
                    selectedGame.value = game
                }
            }

            selectedGame.value?.gameState?.let { gameState ->
                val players = gameState.players.map { it.type }
                PickAPiece(
                    pieces = players,
                    onPick = { pieceType ->
                        val name = selectedGame.value?.currGameName ?: return@PickAPiece
                        val appGame = selectedGame.value

                        scope.launch {
                            try {
                                appGame?.saveEndGame()
                            } catch (e: Exception) {
                                LOGGER.warning("Erro ao salvar estado atual do jogo: ${e.message}")
                            }
                        }

                        val joinedGame = runBlocking { loadGame(name, pieceType) }

                        selectedGame.value = null

                        appState.value = setGame(appState, joinedGame)
                        appState.value = setPage(appState, Page.GAME)
                        appState.value = appState.value.copy(backPage = Page.LOBBY)
                    },

                    onDismiss = {
                        selectedGame.value = null
                        appState.value = setPage(appState, Page.LOBBY)
                    }
                )
            }
        }
    }
}

@Composable
fun Loading() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = PRIMARY, modifier = Modifier.size(56.dp))
            Spacer(Modifier.height(16.dp))
            Text("A carregar jogos...", color = Color.White, fontSize = 18.sp)
        }
    }
}

@Composable
fun Empty() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.SportsEsports, null, Modifier.size(80.dp), Color.White.copy(0.3f))
            Spacer(Modifier.height(16.dp))
            Text("Nenhum jogo guardado", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("Comece um novo jogo", fontSize = 14.sp, color = Color.White.copy(0.6f), textAlign = TextAlign.Center)
        }
    }
}


@Composable
fun PickAPiece(pieces: List<PieceType>, onPick: (PieceType) -> Unit, onDismiss: () -> Unit = {}) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(0.7f))
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onDismiss() },
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                .border(1.dp, Color.White.copy(0.2f), RoundedCornerShape(16.dp))
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
                            PieceType.BLACK -> Color.White.copy(0.3f)
                            PieceType.WHITE -> Color.Black.copy(0.2f)
                        }
                        IconButton(
                            onClick = { onPick(piece) },
                            modifier = Modifier
                                .size(80.dp)
                                .background(color, CircleShape)
                                .border(2.dp, borderColor, CircleShape)
                        ) {}
                    }
                }
            }
        }
    }
}

// Test tags
fun testTagLobbyBoard() = "LobbyBoardPreview"
fun testTagCellPreview(coordinateIndex: Int) = "LobbyCellPreview_$coordinateIndex"
fun testTagCarouselItem(name: String) = "LobbyCarouselItem_$name"