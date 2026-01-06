package pt.isel.reversi.app.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.reversi.app.*
import pt.isel.reversi.app.gameAudio.loadGameAudioPool
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.app.state.setAppState
import pt.isel.reversi.app.state.setLoading
import pt.isel.reversi.core.loadGame

/**
 * Section header composable for organizing settings into logical groups.
 * Displays a title and divider line with the section content below.
 *
 * @param title The section title/header.
 * @param content Lambda for the section's content composables.
 */
@Composable
private fun ReversiScope.SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ReversiText(
            text = title,
            color = getTheme().primaryColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
        HorizontalDivider(
            color = getTheme().textColor.copy(alpha = 0.1f),
            thickness = 1.dp
        )
        content()
    }
}

/**
 * Settings page displaying application configuration options.
 * Includes theme selection and audio volume control.
 *
 * @param appState Global application state for accessing and updating settings.
 */
@Composable
fun SettingsPage(appState: MutableState<AppState>) {
    val draftState = remember { mutableStateOf(appState.value.copy()) }
    var currentVol by remember {
        mutableStateOf(appState.value.audioPool.getMasterVolume() ?: 0f)
    }

    ScaffoldView(
        appState = appState,
        title = "Definições",
        previousPageContent = {
            PreviousPage { appState.setAppState(page = getCurrentState().backPage) }
        }
    ) { padding ->
        val scope = rememberCoroutineScope()
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(0.9f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                GameSection(draftState)

                AudioSection(
                    currentVol = currentVol,
                    onVolumeChange = { currentVol = it },
                )

                AppearanceSection(
                    draftState = draftState,
                    appTheme = appState.value.theme
                )

                ApplyButton {
                    scope.launch {
                        appState.setLoading(true)
                        applySettings(appState, draftState.value, currentVol)
                        appState.setLoading(false)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReversiScope.GameSection(draftState: MutableState<AppState>) {
    SettingsSection(title = "Jogo") {
        ReversiTextField(
            value = draftState.value.playerName ?: "",
            onValueChange = { draftState.setAppState(playerName = it) },
            label = { ReversiText("Nome do Jogador") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReversiScope.AudioSection(
    currentVol: Float,
    onVolumeChange: (Float) -> Unit
) {
    SettingsSection(title = "Áudio") {
        val volumePercent = volumeDbToPercent(currentVol, -20f, 0f)
        val volumeLabel = if (currentVol <= -20f) "Mudo" else "$volumePercent%"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReversiText("Volume Geral", fontSize = 16.sp)
            ReversiText(volumeLabel, fontWeight = FontWeight.Bold)
        }

        Slider(
            value = currentVol,
            valueRange = -20f..0f,
            onValueChange = onVolumeChange,
            colors = SliderDefaults.colors(
                thumbColor = appState.theme.primaryColor,
                activeTrackColor = appState.theme.primaryColor
            )
        )
    }
}

@Composable
private fun ReversiScope.AppearanceSection(
    draftState: MutableState<AppState>,
    appTheme: AppTheme
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsSection(title = "Aspeto Visual") {
        Box(modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                onClick = { expanded = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ReversiText(draftState.value.theme.name)
                    Icon(Icons.Default.Palette, null, tint = appTheme.primaryColor)
                }
            }

            ReversiDropDownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                AppThemes.entries.forEach { entry ->
                    ReversiDropdownMenuItem(
                        text = entry.appTheme.name,
                        onClick = {
                            draftState.setAppState(theme = entry.appTheme)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReversiScope.ApplyButton(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        ReversiButton(text = "Aplicar", onClick = onClick)
    }
}

private suspend fun applySettings(
    appState: MutableState<AppState>,
    draft: AppState,
    volume: Float
) {
    delay(500) // Simulate loading time
    val current = appState.value
    val themeChangedAudio = draft.theme.backgroundMusic != current.theme.backgroundMusic ||
            draft.theme.gameMusic != current.theme.gameMusic

    val oldTheme = current.theme

    val playingAudios = current.audioPool.getPlayingAudios()

    val finalAudioPool = if (themeChangedAudio) {
        current.audioPool.destroy()
        loadGameAudioPool(draft.theme)
    } else current.audioPool

    if (volume <= -20f) {
        finalAudioPool.mute(true)
    } else {
        finalAudioPool.mute(false)
        finalAudioPool.setMasterVolume(volume)
    }

    val currGame = current.game
    val currGameName = currGame.currGameName
    val newGame = if (currGameName != null) {
        currGame.saveEndGame()
        loadGame(currGameName, draft.playerName, currGame.myPiece)
    }
    else currGame

    appState.setAppState(
        game = newGame,
        playerName = draft.playerName,
        theme = draft.theme,
        audioPool = finalAudioPool
    )

    for (audio in playingAudios) {
        val audioToPlay = when (audio) {
            oldTheme.backgroundMusic -> draft.theme.backgroundMusic
            oldTheme.gameMusic -> draft.theme.gameMusic
            else -> null
        }
        if (audioToPlay != null) {
            if (!finalAudioPool.isPlaying(audioToPlay))
                finalAudioPool.play(audioToPlay)
        }
    }
}