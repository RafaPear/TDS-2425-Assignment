package pt.isel.reversi.app.pages

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import pt.isel.reversi.app.state.setError
import pt.isel.reversi.core.exceptions.ErrorType
import pt.isel.reversi.core.exceptions.ReversiException

abstract class ViewModel<T : UiState> {
    protected abstract val _uiState: MutableState<T>
    val uiState: State<T> get() = _uiState
    abstract val globalError: ReversiException?
    abstract val setGlobalError: (Exception?, ErrorType?) -> Unit

    // get() vai sempre buscar ao uiState. Se o uiState for alterado, o error também é alterado
    val error get() = globalError ?: uiState.value.screenState.error

    fun setError(error: Exception?, type: ErrorType? = ErrorType.WARNING) {
        if (globalError != null) {
            setGlobalError(error, type)
        } else {
            _uiState.setError(error, type ?: ErrorType.WARNING)
        }
    }
}
