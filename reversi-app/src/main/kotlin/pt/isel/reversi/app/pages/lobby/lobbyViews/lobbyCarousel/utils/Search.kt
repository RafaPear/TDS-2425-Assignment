package pt.isel.reversi.app.pages.lobby.lobbyViews.lobbyCarousel.utils

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import pt.isel.reversi.app.pages.lobby.PRIMARY

@Composable
fun Search(search: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = search,
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White.copy(alpha = 0.7f),
            unfocusedTextColor = Color.Gray.copy(alpha = 0.7f),
            cursorColor = Color.White.copy(alpha = 0.7f),
            focusedBorderColor = PRIMARY,
            unfocusedBorderColor = Color.White.copy(alpha = 0.7f),
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
        ),
        onValueChange = { onValueChange(it) },
        placeholder = { Text("Procure um jogo...") },
    )
}