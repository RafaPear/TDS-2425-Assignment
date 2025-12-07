package pt.isel.reversi.app.pages.lobby.showGames

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import pt.isel.reversi.app.pages.lobby.PRIMARY

@Composable
fun Search(onValueChange: (String) -> Unit) {
    var search by remember { mutableStateOf("") }

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
        onValueChange = {
            search = it
            onValueChange(search)
        },
        placeholder = { Text("Procure um jogo...") },
    )
}