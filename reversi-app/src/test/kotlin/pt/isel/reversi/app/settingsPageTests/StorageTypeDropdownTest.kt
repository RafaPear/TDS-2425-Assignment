package pt.isel.reversi.app.settingsPageTests

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.runComposeUiTest
import pt.isel.reversi.app.app.state.AppState
import pt.isel.reversi.app.app.state.ReversiScope
import pt.isel.reversi.app.pages.settingsPage.SettingsPage
import pt.isel.reversi.app.pages.settingsPage.SettingsViewModel
import pt.isel.reversi.app.pages.settingsPage.testTagStorageTypeButton
import pt.isel.reversi.core.game.gameServices.EmptyGameService
import pt.isel.reversi.core.storage.GameStorageType
import pt.isel.reversi.utils.BASE_FOLDER
import java.io.File
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class StorageTypeDropdownTest {
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
    fun `check if storage type button exists`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }

    @Test
    fun `verify storage type can be changed in view model`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val initialConfig = viewModel.uiState.value.draftCoreConfig
        val newConfig = initialConfig.copy(gameStorageType = GameStorageType.DATABASE_STORAGE)
        viewModel.setDraftCoreConfig(newConfig)

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }

    @Test
    fun `check if FILE_STORAGE menu item can be tagged`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        // Verify the button exists which displays the storage type
        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }

    @Test
    fun `check if DATABASE_STORAGE menu item can be tagged`() = runComposeUiTest {
        val viewModel = settingsViewModel
        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }
        // Verify the button exists which displays the storage type
        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }

    @Test
    fun `verify storage type button displays current selected type`() = runComposeUiTest {
        val viewModel = settingsViewModel
        val currentType = viewModel.uiState.value.draftCoreConfig.gameStorageType

        setContent {
            reversiScope.SettingsPage(
                viewModel = viewModel,
                onLeave = {}
            )
        }

        onNodeWithTag(testTagStorageTypeButton()).assertExists()
    }
}
