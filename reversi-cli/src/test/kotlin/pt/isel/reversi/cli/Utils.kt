package pt.isel.reversi.cli

import pt.isel.reversi.core.SAVES_FOLDER
import pt.isel.reversi.utils.CONFIG_FOLDER
import java.io.File

fun cleanup(func: () -> Unit) {
    File(CONFIG_FOLDER).deleteRecursively()
    File(SAVES_FOLDER).deleteRecursively()
    func()
    File(CONFIG_FOLDER).deleteRecursively()
    File(SAVES_FOLDER).deleteRecursively()
}