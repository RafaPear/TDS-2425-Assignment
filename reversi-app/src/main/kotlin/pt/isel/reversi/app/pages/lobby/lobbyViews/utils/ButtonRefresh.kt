package pt.isel.reversi.app.pages.lobby.lobbyViews.utils

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pt.isel.reversi.app.MAIN_BACKGROUND_COLOR

@Composable
fun RefreshButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = "Refresh",
            tint = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Preview
@Composable
fun RefreshButtonPreview() {
    Box(
        modifier = Modifier
            .background(MAIN_BACKGROUND_COLOR)
            .fillMaxSize()
    ) {
        RefreshButton { }
    }
}