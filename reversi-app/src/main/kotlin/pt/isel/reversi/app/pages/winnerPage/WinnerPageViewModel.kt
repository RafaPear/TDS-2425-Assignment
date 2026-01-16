package pt.isel.reversi.app.pages.winnerPage

import androidx.compose.runtime.mutableStateOf
import pt.isel.reversi.app.pages.ScreenState
import pt.isel.reversi.app.pages.UiState
import pt.isel.reversi.app.pages.ViewModel
import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.ReversiException
import pt.isel.reversi.core.game.Game
import pt.isel.reversi.core.gameState.Player

data class WinnerUiState(
    override val screenState: ScreenState = ScreenState(),
    val winner: Player? = null,
    val gameName: String? = null,
) : UiState {
    override fun updateScreenState(newScreenState: ScreenState): UiState {
        return copy(screenState = newScreenState)
    }
}

class WinnerPageViewModel(
    val game: Game,
    override val globalError: ReversiException?,
    override val setGlobalError: (Exception?, ErrorType?) -> Unit
) : ViewModel<WinnerUiState>() {
    override val _uiState = mutableStateOf(
        WinnerUiState(
            winner = game.gameState?.winner,
            gameName = game.currGameName,
            screenState = ScreenState(error = globalError)
        )
    )
}