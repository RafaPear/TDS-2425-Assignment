package pt.isel.reversi.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.*
import org.jetbrains.compose.resources.painterResource
import pt.isel.reversi.app.gamePage.GamePage
import pt.isel.reversi.core.*
import pt.isel.reversi.core.board.PieceType
import reversi.reversi_app.generated.resources.Res
import reversi.reversi_app.generated.resources.reversi

fun main() = application {
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        position = WindowPosition.PlatformDefault
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = "Reversi-DEV",
        icon = painterResource(Res.drawable.reversi),
        state = windowState
    ) {

        val game = remember { mutableStateOf(Game()) }
        val page = remember { mutableStateOf(Page.MAIN_MENU) }

        window.minimumSize = java.awt.Dimension(500, 500)

        MenuBar {
            Menu("Ficheiro") {
                Item("Novo Jogo") {
                    page.value = Page.NEW_GAME
                }
                Item("Entrar em Jogo") {
                    page.value = Page.JOIN_GAME
                }
                Item("Guardar Jogo") {
                    page.value = Page.SAVE_GAME
                }
                Item("Definições") {
                    page.value = Page.SETTINGS
                }
                Item("Menu Principal") {
                    page.value = Page.MAIN_MENU
                }
                Item("Jogo Atual") {
                    page.value = Page.GAME
                }
                Separator()
                Item("Sair") {
                    exitApplication()
                }
            }

            Menu("Dev") {
                Item("Mostrar Estado do Jogo") {
                    game.value.printDebugState()
                }
            }

            Menu("Ajuda") {
                Item("Sobre") {
                    page.value = Page.ABOUT
                }
            }
        }

        when (page.value) {
            Page.MAIN_MENU -> MainMenu(page)
            Page.GAME -> GamePage(page, game)
            Page.SETTINGS -> SettingsPage(page)
            Page.ABOUT -> AboutPage(page)
            Page.JOIN_GAME -> JoinGamePage(page, game)
            Page.NEW_GAME -> NewGamePage(page, game)
            Page.SAVE_GAME -> SaveGamePage(page, game)
        }
    }
}

@Composable
fun ErrorDialog(page: MutableState<Page>, errorMessage: String, newPage: Page, onOk: () -> Unit) {
    AlertDialog(
        onDismissRequest = { page.value = newPage; onOk() },
        title = { Text("Erro") },
        text = { Text("Ocorreu um erro: $errorMessage") },
        confirmButton = {
            Button(
                onClick = {
                    page.value = newPage
                    onOk()
                }
            ) {
                Text("OK")
            }
        }
    )
}

@Composable
fun SaveGamePage(page: MutableState<Page>, game: MutableState<Game>) {
    val gameName = remember { mutableStateOf<String?>(null) }
    val isError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Guardar Jogo", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = gameName.value ?: "",
            onValueChange = { gameName.value = it },
            label = { Text("Nome do jogo") },
            singleLine = true
        )

        Button(
            onClick = {
                if (gameName.value?.isNotBlank() ?: false) {
                    try {
                        game.value.saveGame()
                        page.value = Page.GAME
                    } catch (e: Exception) {
                        isError.value = true
                        errorMessage.value = e.message ?: "Erro desconhecido"
                    }
                } else {
                    isError.value = true
                    errorMessage.value = "O nome do jogo não pode estar vazio."
                }
            }
        ) {
            Text("Guardar")
        }

        if (isError.value) {
            ErrorDialog(page, errorMessage.value, Page.SAVE_GAME) {
                isError.value = false
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(onClick = { page.value = Page.GAME }) {
            Text("Voltar")
        }
    }
}


@Composable
fun MainMenu(page: MutableState<Page>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reversi", fontSize = 40.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(40.dp))

        Button(onClick = { page.value = Page.NEW_GAME }) {
            Text("Novo Jogo")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = { page.value = Page.JOIN_GAME }) {
            Text("Entrar em Jogo")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = { page.value = Page.SETTINGS }) {
            Text("Definições")
        }

        Spacer(Modifier.height(20.dp))

        Button(onClick = { page.value = Page.ABOUT }) {
            Text("Sobre")
        }
    }
}

@Composable
fun SettingsPage(page: MutableState<Page>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Definições", fontSize = 30.sp, fontWeight = FontWeight.Bold)

        Text("Opções futuras: som, tema, rede, etc.")

        Spacer(Modifier.height(20.dp))

        Button(onClick = { page.value = Page.MAIN_MENU }) {
            Text("Voltar")
        }
    }
}

@Composable
fun AboutPage(page: MutableState<Page>) {
    Column(
        modifier = Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sobre", fontSize = 30.sp, fontWeight = FontWeight.Bold)
        Text("Projeto Reversi desenvolvido no ISEL.")
        Text("Autores: Rafael Vermelho Pereira e equipa.")
        Text("Versão: DEV Build")

        Spacer(Modifier.height(20.dp))

        Button(onClick = { page.value = Page.MAIN_MENU }) {
            Text("Voltar")
        }
    }
}

@Composable
fun JoinGamePage(page: MutableState<Page>, game: MutableState<Game>) {
    val gameName = remember { mutableStateOf<String?>(null) }
    var desiredType: PieceType? = null
    val isError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Entrar num Jogo", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = gameName.value ?: "",
            onValueChange = { gameName.value = it },
            label = { Text("Nome do jogo") },
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Sou o jogador:")
            Button(onClick = { desiredType = PieceType.BLACK }) { Text("Preto") }
            Button(onClick = { desiredType = PieceType.WHITE }) { Text("Branco") }
        }

        Button(
            onClick = {
                try {
                    game.value.saveGame()
                } catch (e: Exception) {
                    isError.value = true
                    errorMessage.value = e.message ?: "Erro desconhecido"
                }
                if (gameName.value?.isNotBlank() ?: false) {
                    try {
                        game.value = loadGame(
                            gameName = gameName.value!!.trim(),
                            desiredType = desiredType
                        )
                        println("Ligado ao jogo '$gameName'.")
                        page.value = Page.GAME
                    } catch (e: Exception) {
                        isError.value = true
                        errorMessage.value = e.message ?: "Erro desconhecido"
                    }
                } else {
                    isError.value = true
                    errorMessage.value = "O nome do jogo não pode estar vazio."
                }
            }
        ) {
            Text("Entrar")
        }

        Spacer(Modifier.height(10.dp))

        Button(onClick = { page.value = Page.MAIN_MENU }) {
            Text("Voltar")
        }

        if (isError.value) {
            ErrorDialog(page, errorMessage.value, Page.JOIN_GAME) {
                isError.value = false
            }
        }
    }
}

@Composable
fun NewGamePage(page: MutableState<Page>, game: MutableState<Game>) {
    val gameNameState = remember { mutableStateOf<String?>(null) }
    val side = BOARD_SIDE
    val firstTurnState = mutableStateOf(PieceType.BLACK)
    val isError = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Novo Jogo", fontSize = 28.sp, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = gameNameState.value ?: "",
            onValueChange = { gameNameState.value = it },
            label = { Text("Nome do jogo (opcional)") },
            singleLine = true
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Primeiro a jogar:")
            Button(onClick = { firstTurnState.value = PieceType.BLACK }) { Text("Preto") }
            Button(onClick = { firstTurnState.value = PieceType.WHITE }) { Text("Branco") }
        }

        Button(
            onClick = {
                try {
                    game.value.saveGame()
                } catch (e: Exception) {
                    isError.value = true
                    errorMessage.value = e.message ?: "Erro desconhecido"
                }
                try {
                    game.value = if (gameNameState.value?.ifBlank { null } != null) {
                        startNewGame(
                            side = side,
                            players = listOf(Player(firstTurnState.value)),
                            firstTurn = firstTurnState.value,
                            currGameName = gameNameState.value?.ifBlank { null }
                        )
                    } else {
                        startNewGame(
                            side = side,
                            players = listOf(
                                Player(PieceType.BLACK),
                                Player(PieceType.WHITE)
                            ),
                            firstTurn = firstTurnState.value
                        )
                    }
                    println("Novo jogo '${gameNameState.value?.ifBlank { "(local)" } ?: "(local)"} ' iniciado.")
                    page.value = Page.GAME
                } catch (e: Exception) {
                    isError.value = true
                    errorMessage.value = e.message ?: "Erro desconhecido"
                }
            }
        ) {
            Text("Começar Jogo")
        }

        Spacer(Modifier.height(10.dp))

        Button(onClick = { page.value = Page.MAIN_MENU }) {
            Text("Voltar")
        }

        if (isError.value) {
            ErrorDialog(page, errorMessage.value, Page.NEW_GAME) {
                isError.value = false
            }
        }
    }
}

fun Game.printDebugState() {
    println("========== ESTADO ATUAL DO JOGO ==========")
    println("Nome do jogo: ${currGameName ?: "(local)"}")
    println("Modo alvo (target): $target")
    println("Contagem de passes: $countPass")

    val state = gameState
    if (state == null) {
        println("⚠️ Sem estado de jogo carregado.")
        println("==========================================")
        return
    }

    println("\n--- Jogadores ---")
    state.players.forEachIndexed { i, player ->
        println("Jogador ${i + 1}: ${player.type} (${player.points} pontos)")
    }

    println("Último jogador: ${state.lastPlayer}")
    println("Vencedor: ${state.winner?.type ?: "Nenhum"}")

    val board = state.board
    println("\n--- Tabuleiro ---")
    println("Tamanho: ${board.side}x${board.side}")
    println("Peças pretas: ${board.totalBlackPieces}, Peças brancas: ${board.totalWhitePieces}")
    println("Representação:")
    println(this.stringifyBoard())

    println("==========================================\n")
}

