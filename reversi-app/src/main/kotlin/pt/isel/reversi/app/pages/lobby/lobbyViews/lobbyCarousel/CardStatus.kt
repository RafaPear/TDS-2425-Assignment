package pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel

import androidx.compose.ui.graphics.Color
import pt.isel.reversi.core.Game

enum class CardStatus(val text: String, val color: Color) {
    EMPTY("Vazio", Color.Green),
    WAITING_FOR_PLAYERS("Aguardando Jogadores", Color.Yellow),
    FULL("Cheio", Color.Blue),
    CORRUPTED("Corrompido", Color.Red),
    CURRENT_GAME("Jogo Atual", Color.Cyan)
}

fun getCardStatus(game: Game, currentGameName: String?): CardStatus {
    val gameState = game.gameState

    return when {
        gameState == null -> CardStatus.CORRUPTED
        currentGameName == game.currGameName ->
            CardStatus.CURRENT_GAME

        gameState.players.size == 2 -> CardStatus.EMPTY
        gameState.players.size == 1 -> CardStatus.WAITING_FOR_PLAYERS
        gameState.players.isEmpty() -> CardStatus.FULL
        else -> CardStatus.CORRUPTED
    }
}