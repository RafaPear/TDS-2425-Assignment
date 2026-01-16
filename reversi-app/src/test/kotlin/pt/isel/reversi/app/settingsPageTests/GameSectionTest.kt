package pt.isel.reversi.app.settingsPageTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.runComposeUiTest
import pt.isel.reversi.app.app.state.AppState
import pt.isel.reversi.app.app.state.ReversiScope
import pt.isel.reversi.app.pages.settingsPage.SettingsPage
import pt.isel.reversi.app.pages.settingsPage.SettingsViewModel
import pt.isel.reversi.app.pages.settingsPage.testTagGameSection
import pt.isel.reversi.app.pages.settingsPage.testTagPlayerNameTextField
import pt.isel.reversi.core.game.gameServices.EmptyGameService
import pt.isel.reversi.utils.BASE_FOLDER
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class GameSectionTest {
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
    fun `check if Game section exists`() = runComposeUiTest {
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
    fun `check if player name text field exists`() = runComposeUiTest {
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
    fun `verify player name text field is empty initially`() = runComposeUiTest {
        val viewModel = settingsViewModel
        viewModel.setDraftPlayerName("")
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagPlayerNameTextField()).assertExists()
    }

    @Test
    fun `verify player name can be changed in text field`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        val textFieldNode = onNodeWithTag(testTagPlayerNameTextField())
        textFieldNode.assertExists()
        textFieldNode.performTextInput("TestPlayer")
    }
}
