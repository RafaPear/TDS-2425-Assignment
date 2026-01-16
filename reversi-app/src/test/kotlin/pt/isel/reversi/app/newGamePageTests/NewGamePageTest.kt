package pt.isel.reversi.app.newGamePageTests

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.CoroutineScope
import pt.isel.reversi.app.app.state.AppState
import pt.isel.reversi.app.app.state.ReversiScope
import pt.isel.reversi.app.pages.newGamePage.NewGamePage
import pt.isel.reversi.app.pages.newGamePage.NewGameViewModel
import pt.isel.reversi.app.pages.newGamePage.testTagNewGamePage
import pt.isel.reversi.core.game.Game
import pt.isel.reversi.core.game.gameServices.EmptyGameService
import pt.isel.reversi.utils.BASE_FOLDER
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class NewGamePageTest {
    val appState = AppState.empty(EmptyGameService())
    val reversiScope = ReversiScope(appState)

    private fun vmForTest(scope: CoroutineScope) =
        NewGameViewModel(
            scope = scope,
            appState = appState as pt.isel.reversi.app.app.state.AppStateImpl,
            createGame = { _: Game -> },
            setGlobalError = { _, _ -> }
        )

    @BeforeTest
    @AfterTest
    fun cleanUp() {
        File(BASE_FOLDER).deleteRecursively()
    }

    @Test
    fun `check if the New Game page is displayed`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = vmForTest(scope)
            reversiScope.NewGamePage(
                viewModel = viewModel,
                playerNameChange = {},
                onLeave = {}
            )
        }
        onNodeWithTag(testTagNewGamePage()).assertExists()
    }

    @Test
    fun `check if the Local Game checkbox is displayed`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = vmForTest(scope)
            reversiScope.NewGamePage(
                viewModel = viewModel,
                playerNameChange = {},
                onLeave = {}
            )
        }
        onNodeWithTag(testTagNewGamePage()).assertExists()
    }

    @Test
    fun `check if the Board Size text field is displayed`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = vmForTest(scope)
            reversiScope.NewGamePage(
                viewModel = viewModel,
                playerNameChange = {},
                onLeave = {}
            )
        }
        onNodeWithTag(testTagNewGamePage()).assertExists()
    }

    @Test
    fun `check if the Piece dropdown is displayed`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = vmForTest(scope)
            reversiScope.NewGamePage(
                viewModel = viewModel,
                playerNameChange = {},
                onLeave = {}
            )
        }
        onNodeWithTag(testTagNewGamePage()).assertExists()
    }

    @Test
    fun `check if the Start Game button is displayed`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = vmForTest(scope)
            reversiScope.NewGamePage(
                viewModel = viewModel,
                playerNameChange = {},
                onLeave = {}
            )
        }
        onNodeWithTag(testTagNewGamePage()).assertExists()
    }
}
