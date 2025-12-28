package pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.automirrored.rounded.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import pt.isel.reversi.app.pages.lobby.LobbyViewModel
import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.drawCard.GameCard
import pt.isel.reversi.core.Game
import pt.isel.reversi.utils.LOGGER
import kotlin.math.absoluteValue

enum class CardStatus(val text: String, val color: Color) {
    EMPTY("Vazio", Color.Green),
    WAITING_FOR_PLAYERS("Aguardando Jogadores", Color.Yellow),
    FULL("Cheio", Color.Blue),
    CORRUPTED("Corrompido", Color.Red),
    CURRENT_GAME("Jogo Atual", Color.Cyan)
}

private suspend fun PagerState.animateScroll(page: Int) {
    animateScrollToPage(
        page = page,
        animationSpec = spring(
            stiffness = 400f,
            dampingRatio = 0.75f
        )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.ShowGames(
    currentGameName: String?,
    games: List<Game>,
    viewModel: LobbyViewModel,
    buttonRefresh: @Composable () -> Unit = {},
    onGameClick: (Game) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val gamesToShow: List<Game> = remember(games, searchQuery) {
        if (searchQuery.isEmpty())
            games
        else {
            val foundGames = games.filter { it.currGameName?.contains(searchQuery, ignoreCase = true) == true }
            LOGGER.info("Search query: '$searchQuery' - Found : ${foundGames.size}")
            foundGames
        }
    }

    val pagerState = rememberPagerState(pageCount = { gamesToShow.size })
    val scope = rememberCoroutineScope()

    Row {
        Search(searchQuery) { query ->
            scope.launch { pagerState.scrollToPage(0) }
            searchQuery = query
        }

        buttonRefresh()
    }

    Spacer(Modifier.weight(1f))

    BoxWithConstraints {
        LobbyCarousel(
            currentGameName = currentGameName,
            pagerState = pagerState,
            games = gamesToShow,
            viewModel = viewModel,
        ) { game, page ->
            scope.launch {
                if (page != pagerState.currentPage)
                    pagerState.animateScroll(page)
                delay(150L)
                onGameClick(game)
            }
        }

        if (gamesToShow.size > 1) {
            if (pagerState.currentPage > 0) {
                NavButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowBackIos,
                    alignment = Alignment.CenterStart,
                    onClick = {
                        scope.launch {
                            pagerState.animateScroll(pagerState.currentPage - 1)
                        }
                    }
                )
            }
            if (pagerState.currentPage < gamesToShow.size - 1) {
                NavButton(
                    icon = Icons.AutoMirrored.Rounded.ArrowForwardIos,
                    alignment = Alignment.CenterEnd,
                    onClick = {
                        scope.launch {
                            pagerState.animateScroll(pagerState.currentPage + 1)
                        }
                    }
                )
            }
        } else if (gamesToShow.isEmpty()) {
            Text(
                text = "Nenhum jogo encontrado",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }


    Spacer(Modifier.weight(1f))

    val text =
        if (gamesToShow.isEmpty())
            "0 de 0"
        else
            "${pagerState.currentPage + 1} de ${gamesToShow.size}"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PageIndicators(gamesToShow.size, pagerState.currentPage)
        Spacer(Modifier.height(8.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun BoxWithConstraintsScope.LobbyCarousel(
    currentGameName: String?,
    pagerState: PagerState,
    games: List<Game>,
    viewModel: LobbyViewModel,
    onGameClick: (Game, Int) -> Unit
) {
    val availableWidth = this.maxWidth
    val availableHeight = this.maxHeight

    val maxCardWidth = (availableWidth * 0.7f).coerceAtMost(450.dp)
    val maxCardHeight = (availableHeight * 0.8f).coerceAtMost(950.dp)

    val horizontalPadding = (availableWidth / 2 - maxCardWidth / 2)

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = horizontalPadding),
        pageSpacing = 16.dp
    ) { page ->
        val pageOffset =
            (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
        val distance = pageOffset.absoluteValue.coerceIn(0f, 1f)

        val scale = 0.95f + (1f - distance) * 0.12f
        val alpha = 0.2f + (1f - distance) * 0.8f
        val translation = 8.dp * distance

        val game = games[page]
        val gameState = game.gameState

        val cardState = when {
            gameState == null -> CardStatus.CORRUPTED
            currentGameName == game.currGameName ->
                CardStatus.CURRENT_GAME

            gameState.players.size == 2 -> CardStatus.EMPTY
            gameState.players.size == 1 -> CardStatus.WAITING_FOR_PLAYERS
            gameState.players.isEmpty() -> CardStatus.FULL
            else -> CardStatus.CORRUPTED
        }

        LaunchedEffect(
            pagerState.currentPage,
            game.currGameName,
            cardState
        ) {
            if (page != pagerState.currentPage) return@LaunchedEffect

            val gameName = game.currGameName
            LOGGER.info("lobbyCarousel: Refresh iniciado para $gameName")

            val delayMillis = when (cardState) {
                CardStatus.EMPTY,
                CardStatus.CURRENT_GAME -> 100L

                CardStatus.WAITING_FOR_PLAYERS -> 500L
                CardStatus.FULL -> 15_000L
                CardStatus.CORRUPTED -> 20_000L
            }

            try {
                while (isActive && page == pagerState.currentPage) {
                    viewModel.refreshGame(game)
                    delay(delayMillis)
                }
            } finally {
                LOGGER.info("lobbyCarousel: Refresh terminado para $gameName")
            }
        }


        GameCard(
            game = game,
            cardData = cardState,
            enabled = cardState != CardStatus.CORRUPTED,
            modifier = Modifier
                .width(maxCardWidth)
                .height(maxCardHeight)
                .graphicsLayer {
                    this.scaleX = scale
                    this.scaleY = scale
                    this.alpha = alpha
                    this.translationX =
                        if (pageOffset < 0) translation.toPx() else -translation.toPx()
                },
            onClick = { onGameClick(game, page) },
        )
    }
}

@Composable
private fun BoxScope.NavButton(
    icon: ImageVector,
    alignment: Alignment,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .align(alignment)
            .padding(16.dp)
            .size(50.dp)
            .background(Color(0xFF2D2D2D).copy(alpha = 0.9f), CircleShape)
            .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
    ) {
        val icons = listOf(
            Icons.AutoMirrored.Rounded.ArrowBackIos,
            Icons.AutoMirrored.Rounded.ArrowForwardIos
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
private fun PageIndicators(total: Int, current: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(total) { index ->
            val width by animateDpAsState(
                targetValue = if (index == current) 28.dp else 8.dp,
                animationSpec = spring(Spring.DampingRatioMediumBouncy),
                label = "indicator"
            )
            Box(
                Modifier
                    .width(width)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        Color.White.copy(
                            alpha = if (index == current) 1f else 0.3f
                        )
                    )
            )
        }
    }
}