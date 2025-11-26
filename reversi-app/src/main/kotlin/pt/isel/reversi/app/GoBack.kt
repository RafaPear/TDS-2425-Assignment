package pt.isel.reversi.app

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun GoBack(onBack: () -> Unit) {
    Button(
        modifier = Modifier
            .background(color = Color.Red, shape = RoundedCornerShape(size = 20.dp)),
        onClick = onBack,
    ) {
        Text(text = "Go Back")
    }
}