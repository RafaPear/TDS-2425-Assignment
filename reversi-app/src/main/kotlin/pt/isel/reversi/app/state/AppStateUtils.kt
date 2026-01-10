package pt.isel.reversi.app.state

import pt.isel.reversi.app.AppTheme
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.ErrorType.Companion.toReversiException
import pt.isel.reversi.core.exceptions.ReversiException
import pt.isel.reversi.utils.LOGGER

/**
 * Updates the current page in the application state.
 * @param appState The application state holder.
 * @param page The new page to set.
 * @param backPage The new back page (auto-calculated if null).
 */
fun setPage(appState: AppState, page: Page, backPage: Page? = null) {
    if (page == appState.page.value) {
        LOGGER.info("Page is the same: ${page.name}, no changes made")
        return
    }

    val newBackPage = if (page == Page.MAIN_MENU) Page.NONE else backPage ?: appState.page.value
    LOGGER.info("Set page ${page.name}")
    appState.page.value = page
    appState.backPage.value = newBackPage
}

/**
 * Sets the entire app state atomically by updating individual MutableState fields.
 * @param appState The application state holder.
 * @param game The new game state (optional).
 * @param page The new page (optional).
 * @param error The new error (optional).
 * @param backPage The new back page (optional).
 * @param theme The new theme (optional).
 * @param playerName The new player name (optional).
 */
fun setAppState(
    appState: AppState,
    game: Game? = null,
    page: Page? = null,
    error: Exception? = null,
    backPage: Page? = null,
    theme: AppTheme? = null,
    playerName: String? = null,
) {
    LOGGER.info("Set app state")

    if (game != null) {
        appState.game.value = game
    }
    if (page != null) {
        setPage(appState, page, backPage)
    }
    if (error != null) {
        setError(appState, error)
    }
    if (theme != null) {
        appState.theme.value = theme
    }
    if (playerName != null) {
        appState.playerName.value = playerName
    }
}

/**
 * Sets the game state.
 * @param appState The application state holder.
 * @param game The new game state.
 */
fun setGame(appState: AppState, game: Game) {
    LOGGER.info("Set new game state")
    appState.game.value = game
}

/**
 * Retrieves the AudioPool from the current AppState.
 * @param appState The application state holder.
 * @return the AudioPool instance.
 */
fun getStateAudioPool(appState: AppState) = appState.audioPool

/**
 * Updates the error state.
 * @param appState The application state holder.
 * @param error The new error.
 */
fun setError(appState: AppState, error: Exception?) {
    LOGGER.info("Set error: ${error?.message ?: "null"}")
    val newError = if (error is ReversiException) error else error?.toReversiException(ErrorType.CRITICAL)
    appState.error.value = newError
}

/**
 * Updates the loading state.
 * @param appState The application state holder.
 * @param isLoading Whether the app is loading.
 */
fun setLoading(appState: AppState, isLoading: Boolean) {
    if (isLoading == appState.isLoading.value) return
    LOGGER.info("Set loading: $isLoading")
    appState.isLoading.value = isLoading
}

