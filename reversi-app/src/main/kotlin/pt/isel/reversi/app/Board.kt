package pt.isel.reversi.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import pt.isel.reversi.core.BOARD_SIDE

//TODO: Remove hardcoded values
object BoardConfig {
    const val HEIGHT_FRACTION = 0.8f
    const val WIDTH_FRACTION = 0.6f
    val COLOR = Color(0xFF800080)

    val LINE_COLOR = Color.Black
    const val LINE_STROKE_WIDTH = 4f
}

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.PlatformDefault,
    )
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
    ) {
        this.window.minimumSize = java.awt.Dimension(600, 500)
        Board()
    }
}


/** Displays the game board. */
@Preview
@Composable
fun Board() {
    val target = remember { mutableStateOf("On") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Grid()

        Button(
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxHeight(0.6f)
                .fillMaxWidth(0.2f),
            colors = buttonColors(
                containerColor = Color.Blue,
                contentColor = Color.White
            ),
            onClick = { target.value = if (target.value == "On") "Off" else "On" }
        ) {
            BoxWithConstraints(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                val text = "Target ${target.value}"
                val fontSizeHeight = maxHeight.value * 0.4f
                val fontSizeWidth = (maxWidth.value / text.length) * 1.5f
                val fontSize = ((fontSizeHeight + fontSizeWidth) / 2).sp

                Text(
                    text = text,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    maxLines = 1,
                    fontSize = fontSize,
                    autoSize = TextAutoSize.StepBased(minFontSize = 13.sp)
                )
            }

        }
    }
}


/**
 * Draws the board grid.
 */
@Composable
fun Grid() {
    Box(
        modifier = Modifier
            .background(BoardConfig.COLOR)
            .fillMaxHeight(BoardConfig.HEIGHT_FRACTION)
            .fillMaxWidth(BoardConfig.WIDTH_FRACTION),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGrid(BOARD_SIDE, this)
        }
    }
}


/**
 * Draws a grid on the given DrawScope
 * @param side The number of cells on one side of the grid
 * @param drawScope The DrawScope where the grid will be drawn
 */
fun drawGrid(side: Int, drawScope: DrawScope) = with(drawScope) {
    val cellWidth = size.width / side
    val cellHeight = size.height / side

    for (x in 1 until side) {
        drawLine(
            color = BoardConfig.LINE_COLOR,
            strokeWidth = BoardConfig.LINE_STROKE_WIDTH,
            start = Offset(x = x * cellWidth, y = 0f),
            end = Offset(x = x * cellWidth, y = this.size.height),
        )
    }

    for (y in 1 until side) {
        drawLine(
            color = BoardConfig.LINE_COLOR,
            strokeWidth = BoardConfig.LINE_STROKE_WIDTH,
            start = Offset(x = 0f, y = y * cellHeight),
            end = Offset(x = this.size.width, y = y * cellHeight),
        )
    }
}