package pt.isel.reversi.app.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pt.isel.reversi.app.*
import pt.isel.reversi.app.state.AppState
import pt.isel.reversi.app.state.setAppState
import pt.isel.reversi.app.state.setPage
import pt.isel.reversi.core.CoreConfig
import pt.isel.reversi.core.loadCoreConfig
import pt.isel.reversi.core.storage.GameStorageType

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
fun SettingsPage(appState: AppState) {
    val draftState = remember {
        mutableStateOf(
            AppState(
                game = mutableStateOf(appState.game.value),
                page = mutableStateOf(appState.page.value),
                error = mutableStateOf(appState.error.value),
                backPage = mutableStateOf(appState.backPage.value),
                isLoading = mutableStateOf(appState.isLoading.value),
                audioPool = appState.audioPool,
                theme = mutableStateOf(appState.theme.value),
                playerName = mutableStateOf(appState.playerName.value)
            )
        )
    }
    val draftCoreConfig = remember { mutableStateOf(loadCoreConfig()) }
    var currentVol by remember {
        val masterVol = appState.audioPool.getMasterVolume()
        val isMuted = appState.audioPool.isPoolMuted()
        val min = appState.audioPool.getMasterVolumeRange()?.first

        if (isMuted) mutableStateOf(min ?: -20f)
        else mutableStateOf(masterVol ?: 0f)
    }

    ScaffoldView(
        appState = appState,
        title = "Definições",
        previousPageContent = {
            PreviousPage { setPage(appState, getCurrentState().backPage.value) }
        }
    ) { padding ->
        val scope = rememberCoroutineScope()
        val scrollState = rememberScrollState(0)

        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {

            // Scroll Bar for Desktop
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = scrollState
                ),
                style = ScrollbarStyle(
                    minimalHeight = 16.dp,
                    thickness = 8.dp,
                    shape = RoundedCornerShape(4.dp),
                    hoverDurationMillis = 300,
                    unhoverColor = getTheme().primaryColor.copy(alpha = 0.12f),
                    hoverColor = getTheme().primaryColor.copy(alpha = 0.24f)
                )
            )

            Column(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .widthIn(max = 500.dp)
                    .fillMaxWidth(0.9f)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                GameSection(draftState)

                CoreConfigSection(
                    coreConfig = draftCoreConfig.value,
                    onConfigChange = { draftCoreConfig.value = it }
                )

                AudioSection(
                    currentVol = currentVol,
                    onVolumeChange = { currentVol = it },
                )

                AppearanceSection(
                    draftState = draftState,
                    appTheme = appState.theme.value
                )

                ApplyButton {
                    scope.launch {
                        // Placeholder apply: update top-level appState from draft
                        setAppState(
                            appState,
                            playerName = draftState.value.playerName.value,
                            theme = draftState.value.theme.value,
                        )
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
            value = draftState.value.playerName.value ?: "",
            onValueChange = { draftState.value.playerName.value = it },
            label = { ReversiText("Nome do Jogador") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ReversiScope.CoreConfigSection(
    coreConfig: CoreConfig,
    onConfigChange: (CoreConfig) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    SettingsSection(title = "Configuração do Jogo") {
        // Storage Type Dropdown
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
                    ReversiText("Tipo de Armazenamento: ${coreConfig.gameStorageType.name}")
                }
            }

            ReversiDropDownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                GameStorageType.entries.forEach { storageType ->
                    ReversiDropdownMenuItem(
                        text = storageType.name,
                        onClick = {
                            onConfigChange(coreConfig.copy(gameStorageType = storageType))
                            expanded = false
                        }
                    )
                }
            }
        }

        // File Storage Path (only show when FILE_STORAGE is selected)
        if (coreConfig.gameStorageType == GameStorageType.FILE_STORAGE) {
            ReversiTextField(
                value = coreConfig.savesPath,
                onValueChange = { onConfigChange(coreConfig.copy(savesPath = it)) },
                label = { ReversiText("Caminho das Gravações") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Database Settings (only show when DATABASE_STORAGE is selected)
        if (coreConfig.gameStorageType == GameStorageType.DATABASE_STORAGE) {
            ReversiTextField(
                value = coreConfig.dbURI,
                onValueChange = { onConfigChange(coreConfig.copy(dbURI = it)) },
                label = { ReversiText("URI do Banco de Dados") },
                modifier = Modifier.fillMaxWidth()
            )

            ReversiTextField(
                value = coreConfig.dbPort.toString(),
                onValueChange = {
                    val newPort = it.toIntOrNull()
                    if (newPort != null && newPort > 0) {
                        onConfigChange(coreConfig.copy(dbPort = newPort))
                    }
                },
                label = { ReversiText("Porta do Banco de Dados") },
                modifier = Modifier.fillMaxWidth()
            )

            ReversiTextField(
                value = coreConfig.dbName,
                onValueChange = { onConfigChange(coreConfig.copy(dbName = it)) },
                label = { ReversiText("Nome do Banco de Dados") },
                modifier = Modifier.fillMaxWidth()
            )

            ReversiTextField(
                value = coreConfig.dbUser,
                onValueChange = { onConfigChange(coreConfig.copy(dbUser = it)) },
                label = { ReversiText("Usuário do Banco de Dados") },
                modifier = Modifier.fillMaxWidth()
            )

            ReversiTextField(
                value = coreConfig.dbPassword,
                onValueChange = { onConfigChange(coreConfig.copy(dbPassword = it)) },
                label = { ReversiText("Senha do Banco de Dados") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ReversiScope.AudioSection(
    currentVol: Float,
    onVolumeChange: (Float) -> Unit
) {
    SettingsSection(title = "Áudio") {
        val (minVol, maxVol) = appState.audioPool.getMasterVolumeRange() ?: (-20f to 0f)
        val percent = (((currentVol - minVol) / (maxVol - minVol)) * 100).toInt().coerceIn(0, 100)
        val volumeLabel = if (currentVol <= minVol) "Mudo" else "$percent%"

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ReversiText("Volume Geral", fontSize = 16.sp)
            ReversiText(volumeLabel, fontWeight = FontWeight.Bold)
        }

        Slider(
            value = currentVol,
            valueRange = minVol..maxVol,
            onValueChange = onVolumeChange,
            colors = SliderDefaults.colors(
                thumbColor = appState.theme.value.primaryColor,
                activeTrackColor = appState.theme.value.primaryColor,
                inactiveTrackColor = appState.theme.value.textColor.copy(alpha = 0.3f)
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
                    ReversiText(draftState.value.theme.value.name)
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
                            draftState.value.theme.value = entry.appTheme
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

// applySettings left for later refinement
