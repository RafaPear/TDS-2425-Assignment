package pt.isel.reversi.app.gamePage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.reversi.app.Page
import pt.isel.reversi.core.Game
import pt.isel.reversi.core.board.Coordinate

@Composable
fun GamePage(page: MutableState<Page>, game: MutableState<Game>) {
    val isError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BOARD_BACKGROUND_COLOR)
            .padding(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            if (game.value.currGameName != null)
                Text(
                    text = "Game: ${game.value.currGameName}",
                    color = TEXT_COLOR,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    autoSize = TextAutoSize.StepBased(
                        maxFontSize = 50.sp
                    ),
                    maxLines = 1,
                    softWrap = false,
                )
        }

        Spacer(modifier = Modifier.height(padding))

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(BOARD_SIDE_COLOR, shape = RoundedCornerShape(12.dp))
                    .padding(10.dp),
            ) {
                DrawBoard(game) { x, y ->
                    try {
                        game.value = game.value.play(Coordinate(x, y))
                    } catch (e: Exception) {
                        isError.value = true
                        errorMessage.value = e.message ?: "Erro desconhecido"
                    }
                }
            }

            Spacer(modifier = Modifier.width(padding))

            Column(
                modifier = Modifier
                    .fillMaxSize(),
            ) {

                TextPlayersScore(
                    state = game.value.gameState,
                    page = page,
                    isError = isError,
                    errorMessage = errorMessage
                )

                Spacer(modifier = Modifier.height(padding))

                val target = if (game.value.target) "On" else "Off"

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){// Button to toggle the target state
                    GameButton("Target $target") {
                        game.value = game.value.setTargetMode(!game.value.target)
                    }

                    Spacer(modifier = Modifier.height(padding))

                    // Main action button
                    GameButton("Update") {
                        game.value = game.value.refresh()
                    }
                }

            }
        }
    }
}