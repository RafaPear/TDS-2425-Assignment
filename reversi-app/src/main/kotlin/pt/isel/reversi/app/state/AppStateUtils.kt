package pt.isel.reversi.app.state

import androidx.compose.runtime.MutableState
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.exceptions.ReversiException
import pt.isel.reversi.utils.LOGGER
import pt.isel.reversi.utils.audio.AudioPool

fun setGame(appState: MutableState<AppState>, game: Game): AppState {
    LOGGER.info("Set new game state")
    return appState.value.copy(game = game)
}

fun setPage(appState: MutableState<AppState>, page: Page): AppState {
    LOGGER.info("Set page ${page.name}")
    val backPage = setBackPage(appState, newPage = page)
    return appState.value.copy(page = page, backPage = backPage)
}

fun setError(appState: MutableState<AppState>, error: ReversiException?): AppState {
    LOGGER.info("Set error: ${error?.message ?: "null"}")
    return appState.value.copy(error = error)
}

fun setAppState(
    appState: MutableState<AppState>,
    game: Game = appState.value.game,
    page: Page = appState.value.page,
    error: ReversiException? = appState.value.error,
    audioPool: AudioPool = appState.value.audioPool,
) = AppState(game, page, error, backPage = setBackPage(appState, newPage = page), audioPool)

/**
 * Retrieves the [AudioPool] from the current [AppState].
 *
 * @param appState the mutable state holding the current [AppState]
 * @return the [AudioPool] instance from the [AppState]
 */
fun getStateAudioPool(appState: MutableState<AppState>) = appState.value.audioPool

private fun setBackPage(appState: MutableState<AppState>, newPage: Page): Page {
    val page = appState.value.page
    LOGGER.info("Set back page: ${page.name}")
    return when (newPage) {
        Page.GAME -> Page.MAIN_MENU
        else -> page
    }
}