import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.runBlocking
import pt.isel.reversi.app.exceptions.GameNotStartedYet
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.app.state.Page
import pt.isel.reversi.app.state.setAppState
import pt.isel.reversi.app.state.setPage
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.loadCoreConfig
import pt.isel.reversi.core.startNewGame
import pt.isel.reversi.utils.audio.AudioPool
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class AppStateTests {
    fun cleanup(func: suspend () -> Unit) {
        val conf = loadCoreConfig()
        File(conf.SAVES_FOLDER).deleteRecursively()
        runBlocking { func() }
        File(conf.SAVES_FOLDER).deleteRecursively()
    }
    @Test
    fun `setPage update the same page does not change the state`() {
        cleanup {
            val expectedAppState = mutableStateOf(
                value = AppState(
                    page = Page.JOIN_GAME,
                    game = runBlocking {
                        startNewGame(
                            side = 8,
                            players = listOf(Player(type = PieceType.BLACK)),
                            firstTurn = PieceType.BLACK,
                        )
                    },
                    error = null,
                    audioPool = AudioPool(emptyList())
                )
            )

            val uut = setPage(expectedAppState, Page.JOIN_GAME)
            assertEquals(expectedAppState.value, uut)
        }
    }

    @Test
    fun `setAppState the same page does not change the backPageState`() {
        cleanup {
            val expectedAppState = mutableStateOf(
                value = AppState(
                    page = Page.JOIN_GAME,
                    game = runBlocking {
                        startNewGame(
                            side = 8,
                            players = listOf(Player(type = PieceType.BLACK)),
                            firstTurn = PieceType.BLACK,
                        )
                    },
                    error = GameNotStartedYet(),
                    audioPool = AudioPool(emptyList()),
                    backPage = Page.MAIN_MENU
                )
            )

            val uut = setAppState(expectedAppState, page = Page.JOIN_GAME, error = GameNotStartedYet())
            assertEquals(expectedAppState.value.backPage, uut.backPage)
        }
    }
}