package pt.isel.reversi.app.settingsPageTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import pt.isel.reversi.app.app.state.AppState
import pt.isel.reversi.app.app.state.ReversiScope
import pt.isel.reversi.app.pages.settingsPage.*
import pt.isel.reversi.core.game.gameServices.EmptyGameService
import pt.isel.reversi.core.storage.GameStorageType
import pt.isel.reversi.utils.BASE_FOLDER
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DatabaseConfigTest {
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
    fun `check if database URI text field appears for DATABASE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbURITextField()).assertExists()
    }

    @Test
    fun `check if database port text field appears for DATABASE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbPortTextField()).assertExists()
    }

    @Test
    fun `check if database name text field appears for DATABASE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbNameTextField()).assertExists()
    }

    @Test
    fun `check if database user text field appears for DATABASE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbUserTextField()).assertExists()
    }

    @Test
    fun `check if database password text field appears for DATABASE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbPasswordTextField()).assertExists()
    }

    @Test
    fun `check that database fields do not appear for FILE_STORAGE`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val coreConfig = viewModel.uiState.value.draftCoreConfig
        viewModel.setDraftCoreConfig(coreConfig.copy(gameStorageType = GameStorageType.FILE_STORAGE))

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagDbURITextField()).assertDoesNotExist()
        onNodeWithTag(testTagDbPortTextField()).assertDoesNotExist()
        onNodeWithTag(testTagDbNameTextField()).assertDoesNotExist()
        onNodeWithTag(testTagDbUserTextField()).assertDoesNotExist()
        onNodeWithTag(testTagDbPasswordTextField()).assertDoesNotExist()
    }
}
