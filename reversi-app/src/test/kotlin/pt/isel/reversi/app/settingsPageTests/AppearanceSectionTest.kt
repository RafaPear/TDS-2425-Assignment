package pt.isel.reversi.app.settingsPageTests

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runComposeUiTest
import kotlinx.coroutines.CoroutineScope
import pt.isel.reversi.app.app.state.AppState
import pt.isel.reversi.app.app.state.ReversiScope
import pt.isel.reversi.app.pages.settingsPage.*
import pt.isel.reversi.core.game.gameServices.EmptyGameService
import pt.isel.reversi.utils.BASE_FOLDER
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class AppearanceSectionTest {
    val appState = AppState.empty(EmptyGameService())
    val reversiScope = ReversiScope(appState)

    fun settingsViewModel(scope: CoroutineScope) =
        SettingsViewModel(
            scope = scope,
            appState = appState as pt.isel.reversi.app.app.state.AppStateImpl,
            setTheme = {},
            setPlayerName = {},
            saveGame = {},
            setGame = {},
            setGlobalError = { _, _ -> }
        )

    @BeforeTest
    @AfterTest
    fun cleanUp() {
        File(BASE_FOLDER).deleteRecursively()
    }

    @Test
    fun `check if Appearance section exists`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagAppearanceSection()).assertExists()
    }

    @Test
    fun `check if theme button exists`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagThemeButton()).assertExists()
    }

    @Test
    fun `check if theme dropdown exists when menu is opened`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }

        onNodeWithTag(testTagThemeButton()).performClick()
        onNodeWithTag(testTagThemeDropdown()).assertExists()
    }
}
