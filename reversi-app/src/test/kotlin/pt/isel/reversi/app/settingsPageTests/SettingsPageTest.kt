package pt.isel.reversi.app.settingsPageTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
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
class SettingsPageTest {
    val appState = AppState.empty(EmptyGameService())
    val reversiScope = ReversiScope(appState)

    private val settingsViewModel: SettingsViewModel
        get() = SettingsViewModel(
            scope = kotlinx.coroutines.GlobalScope,
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
    fun `check if the Settings page is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagSettingsPage()).assertExists()
    }

    @Test
    fun `check if the Game section is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagGameSection()).assertExists()
    }

    @Test
    fun `check if the player name text field is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagPlayerNameTextField()).assertExists()
    }

    @Test
    fun `check if the Core Config section is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagCoreConfigSection()).assertExists()
    }

    @Test
    fun `check if the Audio section is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagAudioSection()).assertExists()
    }

    @Test
    fun `check if the Appearance section is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagAppearanceSection()).assertExists()
    }

    @Test
    fun `check if the Apply button is displayed`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagApplyButton()).assertExists()
    }
}
