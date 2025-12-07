package pt.isel.reversi.app.pages.lobby

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun RefreshButton(onClick: () -> Unit) {
    Button(
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.Black
        ),
        onClick = {
            onClick()
        }
    ) {

    }
}