package pt.isel.reversi.app.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import pt.isel.reversi.app.AppTheme
import pt.isel.reversi.app.AppThemes
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.exceptions.ReversiException
import pt.isel.reversi.utils.audio.AudioPool

/**
 * Central application state with each field as a mutable state.
 * This allows fine-grained reactivity: changing one field only invalidates
 * composables that read that specific field.
 */
data class AppState(
    val game: MutableState<Game>,
    val page: MutableState<Page>,
    val error: MutableState<ReversiException?>,
    val backPage: MutableState<Page>,
    val isLoading: MutableState<Boolean>,
    val audioPool: AudioPool,
    val theme: MutableState<AppTheme>,
    val playerName: MutableState<String?>
) {
    companion object {
        // Empty AppState for initialization
        fun empty(): AppState = AppState(
            game = mutableStateOf(Game()),
            page = mutableStateOf(Page.MAIN_MENU),
            error = mutableStateOf(null),
            backPage = mutableStateOf(Page.MAIN_MENU),
            isLoading = mutableStateOf(false),
            audioPool = AudioPool(emptyList()),
            theme = mutableStateOf(AppThemes.DARK.appTheme),
            playerName = mutableStateOf(null)
        )
    }
}