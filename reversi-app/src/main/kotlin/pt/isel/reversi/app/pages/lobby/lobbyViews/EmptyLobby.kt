package pt.isel.reversi.app.pages.lobby.lobbyViews

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun ColumnScope.Empty(buttonRefresh: @Composable () -> Unit = {}) {
    Icon(
        Icons.Filled.SportsEsports,
        contentDescription = null,
        modifier = Modifier.size(80.dp),
        tint = Color.White.copy(alpha = 0.3f)
    )
    Spacer(Modifier.height(16.dp))
    Text(
        "Nenhum jogo guardado",
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
    Spacer(Modifier.height(8.dp))
    Text(
        "Comece um novo jogo",
        fontSize = 14.sp,
        color = Color.White.copy(alpha = 0.6f),
        textAlign = TextAlign.Center
    )
    buttonRefresh()
}
