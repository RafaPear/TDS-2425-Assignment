package pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.drawCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.reversi.app.ReversiScope
import pt.isel.reversi.app.ReversiText
import pt.isel.reversi.app.getTheme
import pt.isel.reversi.app.pages.game.utils.DrawBoard
import pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.CardStatus
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType

fun cardTestTag(gameId: String) = "game_card_$gameId"

fun headerBadgeTestTag(gameId: String) = "header_badge_$gameId"

fun scorePanelTestTag(gameId: String) = "score_panel_$gameId"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReversiScope.GameCard(
    game: Game,
    enabled: Boolean,
    cardData: CardStatus,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val name = game.currGameName ?: return
    val state = game.gameState ?: return

    val statusText = cardData.text
    val statusColor = cardData.color

    Card(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.shadow(16.dp, RoundedCornerShape(24.dp))
            .testTag(cardTestTag(name)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(0.1f)),
        colors = CardDefaults.cardColors(
            containerColor = getTheme().backgroundColor,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(getTheme().secondaryColor.copy(.2f))
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeaderBadge(statusText, statusColor, name)

            DrawBoard(
                false,
                state,
                modifier = Modifier.weight(4f), // Preenche o Box
                true,
                { emptyList() },
                {}
            )

            ScorePanel(Modifier.testTag(scorePanelTestTag(name)), state.board)
        }
    }
}


@Composable
private fun ReversiScope.HeaderBadge(statusText: String, statusColor: Color, name: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .testTag(headerBadgeTestTag(name)),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        ReversiText(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        StatusBadge(statusText, statusColor)
    }
}

@Composable
private fun ReversiScope.StatusBadge(text: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp), color = color.copy(0.2f)
    ) {
        ReversiText(
            text = text,
            color = color,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun ReversiScope.ScorePanel(modifier: Modifier, board: Board) {
    Row(
        modifier = modifier.fillMaxWidth().background(getTheme().secondaryColor.copy(.2f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp)).padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ScoreItem(PieceType.BLACK, board.totalBlackPieces)
        ScoreItem(PieceType.WHITE, board.totalWhitePieces)
    }
}

@Composable
private fun ReversiScope.ScoreItem(type: PieceType, score: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            Modifier.size(40.dp).background(
                if (type == PieceType.BLACK) getTheme().darkPieceColor else getTheme().lightPieceColor, CircleShape
            ).border(
                2.dp,
                if (type == PieceType.BLACK) getTheme().darkPieceColor.copy(0.2f) else getTheme().lightPieceColor.copy(
                    0.2f
                ),
                CircleShape
            )
        )
        Spacer(Modifier.height(8.dp))
        ReversiText(
            text = score.toString(), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
        )
    }
}