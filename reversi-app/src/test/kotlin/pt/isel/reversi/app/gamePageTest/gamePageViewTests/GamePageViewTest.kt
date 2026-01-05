package pt.isel.reversi.app.gamePageTest.gamePageViewTests

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.runBlocking
import pt.isel.reversi.app.ReversiScope
import pt.isel.reversi.app.pages.game.GamePageView
import pt.isel.reversi.app.pages.game.testTagGamePage
import pt.isel.reversi.app.pages.game.testTagPlayerScore
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.app.state.Page
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.startNewGame
import pt.isel.reversi.utils.audio.AudioPool
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class GamePageViewTest {
    val game = runBlocking {
        startNewGame(
            side = 4,
            players = listOf(Player(type = PieceType.BLACK), Player(type = PieceType.WHITE)),
            firstTurn = PieceType.BLACK,
            currGameName = null
        )
    }

    val reversiScope = ReversiScope(AppState.EMPTY_APP_STATE)

    @Test
    fun `check if the GamePage is displayed`() = runComposeUiTest {
        val expectedAppState = AppState(
            game = game,
            page = Page.GAME,
            error = null,
            audioPool = AudioPool(emptyList()),
            theme = AppState.EMPTY_APP_STATE.theme
        )

        val appState = mutableStateOf(value = expectedAppState)

        setContent {
            reversiScope.GamePageView(
                game = appState.value.game,
                freeze = false,
                onCellClick = {},
                getAvailablePlays = { emptyList() },
                setTargetMode = {},
                pass = {}
            )
        }
        onNodeWithTag(testTag = testTagGamePage())
            .assertExists()
    }

    @Test
    fun `check if have a board`() = runComposeUiTest {
        val expectedAppState = AppState(
            game = game,
            page = Page.GAME,
            error = null,
            audioPool = AudioPool(emptyList()),
            theme = AppState.EMPTY_APP_STATE.theme
        )

        val appState = mutableStateOf(value = expectedAppState)

        setContent {
            reversiScope.GamePageView(
                game = appState.value.game,
                freeze = false,
                onCellClick = {},
                getAvailablePlays = { emptyList() },
                setTargetMode = {},
                pass = {}
            )
        }
        onNodeWithTag(testTag = testTagGamePage()).assertExists()
    }

    @Test
    fun `check if have a two players on Score`() = runComposeUiTest {
        val expectedAppState = AppState(
            game = game,
            page = Page.GAME,
            error = null,
            audioPool = AudioPool(emptyList()),
            theme = AppState.EMPTY_APP_STATE.theme
        )

        val appState = mutableStateOf(value = expectedAppState)

        setContent {
            reversiScope.GamePageView(
                game = appState.value.game,
                freeze = false,
                onCellClick = {},
                getAvailablePlays = { emptyList() },
                setTargetMode = {},
                pass = {}
            )
        }

        val players = game.gameState?.players

        players?.forEach { player ->
            onNodeWithTag(testTag = testTagPlayerScore(player)).assertExists()
        }
    }

    @Test
    fun `check if don't have players on Score when gameState is null`() = runComposeUiTest {
        val expectedAppState = AppState(
            game = game.copy(gameState = null),
            page = Page.GAME,
            error = null,
            audioPool = AudioPool(emptyList()),
            theme = AppState.EMPTY_APP_STATE.theme
        )

        setContent {
            reversiScope.GamePageView(
                game = expectedAppState.game,
                freeze = false,
                onCellClick = {},
                getAvailablePlays = { emptyList() },
                setTargetMode = {},
                pass = {}
            )
        }

        onNodeWithTag(testTag = testTagPlayerScore(game.gameState?.players[0]!!))
            .assertDoesNotExist()
    }
}