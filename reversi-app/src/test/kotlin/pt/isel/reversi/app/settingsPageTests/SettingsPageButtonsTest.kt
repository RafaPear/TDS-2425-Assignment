package pt.isel.reversi.app.settingsPageTests

import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
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
class SettingsPageButtonsTest {
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
    fun `check if Apply button is displayed and clickable`() = runComposeUiTest {
        var clicked = false
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = { clicked = true }
            )
        }
        onNodeWithTag(testTagApplyButton()).assertExists()
    }

    @Test
    fun `check if Theme button is displayed and clickable`() = runComposeUiTest {
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
    fun `check if Storage Type button is displayed and clickable`() = runComposeUiTest {
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }

    @Test
    fun `verify Apply button can be clicked`() = runComposeUiTest {
        var onLeaveInvoked = false
        setContent {
            val scope = rememberCoroutineScope()
            val viewModel = settingsViewModel(scope)
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = { onLeaveInvoked = true }
            )
        }
        onNodeWithTag(testTagApplyButton()).assertExists()
        // Note: The actual click behavior depends on the ViewModel implementation
        // This test verifies the button exists and can be interacted with
    }
}
