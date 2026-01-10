package pt.isel.reversi.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.runBlocking
import pt.isel.reversi.app.state.*
import pt.isel.reversi.core.Game
import pt.isel.reversi.utils.LOGGER

/**
 * Creates the application menu bar with File, View, Dev, and Help menus.
 * Provides navigation to different pages and application controls.
 *
 * @param appState Global application state for navigation and configuration.
 * @param windowState Window state for toggling fullscreen mode.
 * @param exitAction Callback function to execute on application exit.
 */
@Composable
fun FrameWindowScope.MakeMenuBar(appState: AppState, windowState: WindowState, exitAction: () -> Unit) {
    MenuBar {
        Menu("Ficheiro") {
            Item("Novo Jogo") {
                setPage(appState, Page.NEW_GAME)
            }
            Item("Guardar Jogo") {
                setPage(appState, Page.SAVE_GAME)
            }
            Item("Definições") {
                setPage(appState, Page.SETTINGS)
            }
            Item("Menu Principal") {
                setPage(appState, Page.MAIN_MENU)
            }
            Item("Jogo Atual") {
                setPage(appState, Page.GAME)
            }
            Item("Sair do jogo atual") {
                runBlocking { appState.game.value.saveEndGame() }
                setPage(appState, Page.MAIN_MENU)
                setGame(appState, Game())
            }
            Separator()
            Item("Sair") {
                exitAction()
            }
        }

        Menu("View") {
            Item("Toggle Fullscreen") {
                windowState.placement =
                    if (windowState.placement == WindowPlacement.Floating) WindowPlacement.Fullscreen
                    else WindowPlacement.Floating
                appState.theme.value = appState.theme.value.copy() // Force recomposition
            }
        }

        Menu("Dev") {
            Item("Mostrar Estado do Jogo") {
                // Use the extension function defined in the pt.isel.reversi.app package
                appState.game.value.printDebugState()
            }
            Item("Nullify Game State") {
                setGame(
                    appState,
                    Game()
                )
                LOGGER.info("Estado do jogo anulado para fins de teste.")
            }
            Item("Reload Config") {
                try {
                    setGame(
                        appState,
                        appState.game.value.reloadConfig()
                    )
                    LOGGER.info("Config recarregada com sucesso.")
                } catch (e: Exception) {
                    setError(appState, error = e)
                }
            }
            Item("Trigger Error") {
                setError(appState, error = Exception("Erro de teste disparado a partir do menu Dev"))
            }
            Item("Crash App") {
                throw RuntimeException("App crash triggered from Dev menu")
            }
        }

        Menu("Ajuda") {
            Item("Sobre") {
                setPage(appState, Page.ABOUT)
            }
        }
    }
}
