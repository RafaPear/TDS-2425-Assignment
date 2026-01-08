package lobbyMenuTest.lobbyCarouselTests

import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.CardStatus
import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.getCardStatus
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.storage.GameState
import kotlin.test.Test
import kotlin.test.assertEquals

class CardStatusTests {
    @Test
    fun `getCardStatus returns CORRUPTED for game with null state`() {
        val game = Game(currGameName = "testGame", gameState = null)
        val expected = CardStatus.CORRUPTED

        val result = getCardStatus(game, game.currGameName)
        assertEquals(expected, result)
    }

    @Test
    fun `getCardStatus returns CURRENT_GAME for current game`() {
        val gameState = GameState(
            board = Board(4).startPieces(),
            players = listOf(Player(PieceType.BLACK)),
            lastPlayer = PieceType.WHITE
        )
        val game = Game(currGameName = "testGame", gameState = gameState)
        val expected = CardStatus.CURRENT_GAME

        val result = getCardStatus(game, game.currGameName)
        assertEquals(expected, result)
    }

    @Test
    fun `getCardStatus returns FULL for game with no players`() {
        val gameState = GameState(
            board = Board(4).startPieces(),
            players = emptyList(),
            lastPlayer = PieceType.WHITE
        )
        val game = Game(currGameName = "testGame", gameState = gameState)
        val expected = CardStatus.FULL

        val result = getCardStatus(game, "anotherGame")
        assertEquals(expected, result)
    }

    @Test
    fun `getCardStatus returns WAITING_FOR_PLAYERS for game with one player`() {
        val gameState = GameState(
            board = Board(4).startPieces(),
            players = listOf(Player(PieceType.BLACK)),
            lastPlayer = PieceType.WHITE
        )
        val game = Game(currGameName = "testGame", gameState = gameState)
        val expected = CardStatus.WAITING_FOR_PLAYERS
        val result = getCardStatus(game, "anotherGame")
        assertEquals(expected, result)
    }

    @Test
    fun `getCardStatus returns EMPTY for game with two players`() {
        val gameState = GameState(
            board = Board(4).startPieces(),
            players = listOf(Player(PieceType.BLACK), Player(PieceType.WHITE)),
            lastPlayer = PieceType.BLACK
        )
        val game = Game(currGameName = "testGame", gameState = gameState)
        val expected = CardStatus.EMPTY
        val result = getCardStatus(game, "anotherGame")
        assertEquals(expected, result)
    }
}