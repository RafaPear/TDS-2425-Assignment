package lobbyMenuTest.lobbyCarouselTests.lobbyCarouselViewsTests

import androidx.compose.ui.test.*
import pt.isel.reversi.app.ReversiScope
import pt.isel.reversi.app.pages.game.testTagBoard
import pt.isel.reversi.app.pages.game.testTagCellView
import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.drawCard.*
import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.getCardStatus
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.storage.GameState
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class DrawCardTests {
    val game = Game(
        currGameName = "TestGame",
        gameState = GameState(
            players = listOf(Player(PieceType.WHITE)),
            lastPlayer = PieceType.BLACK,
            board = Board(4).startPieces()
        )
    )

    val reversiScope = ReversiScope(AppState.EMPTY_APP_STATE)

    @Test
    fun `verify if drawCard is displayed`() = runComposeUiTest {
        val name = game.currGameName!!
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(name)).assertExists()
    }

    @Test
    fun `verify if drawCard is displayed correctly`() = runComposeUiTest {
        val name = game.currGameName!!
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(name)).assertExists()
        onNodeWithTag(headerBadgeTestTag(name), true).assertExists()
        onNodeWithTag(testTagBoard(), true).assertExists()
        onNodeWithTag(scorePanelTestTag(name), true).assertExists()
    }

    @Test
    fun `verify if drawCard is not displayed when game name is null`() = runComposeUiTest {
        val invalidGame = game.copy(currGameName = null)
        setContent {
            reversiScope.GameCard(
                game = invalidGame,
                enabled = false,
                cardData = getCardStatus(invalidGame, ""),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(""), true).assertDoesNotExist()
    }

    @Test
    fun `verify if drawCard is not displayed when game state is null`() = runComposeUiTest {
        val invalidGame = game.copy(gameState = null)
        setContent {
            reversiScope.GameCard(
                game = invalidGame,
                enabled = false,
                cardData = getCardStatus(invalidGame, invalidGame.currGameName!!),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(invalidGame.currGameName!!), true).assertDoesNotExist()
    }

    @Test
    fun `verify if Card is enabled`() = runComposeUiTest {
        val name = game.currGameName!!
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = true,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(name)).assertIsEnabled()
    }

    @Test
    fun `verify if Card is disabled`() = runComposeUiTest {
        val name = game.currGameName!!
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        onNodeWithTag(cardTestTag(name)).assertIsNotEnabled()
    }

    @Test
    fun `veify id header badge displays correct game name and status`() = runComposeUiTest {
        val name = game.currGameName!!
        val cardStatus = getCardStatus(game, name)
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = cardStatus,
                onClick = {}
            )
        }

        onNodeWithTag(statusTextTestTag(name), true).assertTextContains(name)
        onNodeWithTag(statusBadgeTestTag(name), true).assertTextContains(cardStatus.text)
    }

    @Test
    fun `verify if board is not clickable`() = runComposeUiTest {
        val name = game.currGameName!!

        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        val valideCoords = game.getAvailablePlays().first()
        onNodeWithTag(testTagBoard(), true).assertHasNoClickAction()
        onNodeWithTag(testTagCellView(valideCoords), true).assertHasNoClickAction()
    }

    @Test
    fun `veify if score panel displays correct scores and pieces`() = runComposeUiTest {
        val name = game.currGameName!!
        setContent {
            reversiScope.GameCard(
                game = game,
                enabled = false,
                cardData = getCardStatus(game, name),
                onClick = {}
            )
        }

        val board = game.gameState!!.board
        val totalBlack = board.totalBlackPieces
        val totalWhite = board.totalWhitePieces

        val blackPieceTestTag = scoreItemPieceTestTag(
            scorePainelTestTag = scorePanelTestTag(gameId = name),
            pieceType = PieceType.BLACK
        )

        val whitePieceTestTag = scoreItemPieceTestTag(
            scorePainelTestTag = scorePanelTestTag(gameId = name),
            pieceType = PieceType.WHITE
        )

        val blackPiecesScoreTestTag = scoreItemScoreTestTag(
            scorePainelTestTag = scorePanelTestTag(gameId = name),
            pieceType = PieceType.BLACK,
            score = totalBlack
        )

        val whitePiecesScoreTestTag = scoreItemScoreTestTag(
            scorePainelTestTag = scorePanelTestTag(gameId = name),
            pieceType = PieceType.WHITE,
            score = totalWhite
        )

        onNodeWithTag(blackPieceTestTag, true).assertExists()
        onNodeWithTag(whitePieceTestTag, true).assertExists()
        onNodeWithTag(blackPiecesScoreTestTag, true).assertTextContains(totalBlack.toString())
        onNodeWithTag(whitePiecesScoreTestTag, true).assertTextContains(totalWhite.toString())
    }
}