package pt.isel.reversi.app

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pt.isel.reversi.app.pages.JoinGamePage
import pt.isel.reversi.app.pages.MainMenu
import pt.isel.reversi.app.pages.NewGamePage
import pt.isel.reversi.app.pages.game.GamePage
import pt.isel.reversi.app.pages.lobby.LobbyMenu
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.app.state.Page

/**
 * Hierarquia das páginas (ordem de profundidade)
 */
private val pageHierarchy = mapOf(
    Page.MAIN_MENU to 0,  // Raiz
    Page.LOBBY     to 1,
    Page.NEW_GAME  to 1,
    Page.JOIN_GAME to 1,
    Page.SETTINGS  to 1,
    Page.ABOUT     to 1,
    Page.SAVE_GAME to 1,
    Page.GAME      to 2   // Mais profundo
)

/**
 * Determina se está a avançar (push) ou voltar (pop)
 */
private fun isForward(from: Page, to: Page): Boolean {
    val fromLevel = pageHierarchy[from] ?: 0
    val toLevel = pageHierarchy[to] ?: 0
    return toLevel > fromLevel
}

/**
 * Componente central que gere transições entre páginas.
 * Desliza para a direita (forward) ou esquerda (backward).
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppScreenSwitcher(appState: MutableState<AppState>) {
    val targetPage = appState.value.page

    AnimatedContent(
        targetState = targetPage,
        transitionSpec = {
            val forward = isForward(initialState, targetState)

            if (forward) {
                // Avançar: entra da direita, sai para esquerda
                slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            } else {
                // Voltar: entra da esquerda, sai para direita
                slideInHorizontally(
                    initialOffsetX = { -it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                ) togetherWith slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(300, easing = FastOutSlowInEasing)
                )
            }
        },
        modifier = Modifier.fillMaxSize(),
        label = "PageTransition"
    ) { page ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
        ) {
            when (page) {
                Page.MAIN_MENU -> MainMenu(appState)
                Page.GAME      -> GamePage(appState)
                Page.SETTINGS  -> SettingsPage(appState)
                Page.ABOUT     -> AboutPage(appState)
                Page.JOIN_GAME -> JoinGamePage(appState)
                Page.NEW_GAME  -> NewGamePage(appState)
                Page.SAVE_GAME -> SaveGamePage(appState)
                Page.LOBBY     -> LobbyMenu(appState)
            }
        }
    }
}