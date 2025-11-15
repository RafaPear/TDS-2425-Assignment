package pt.isel.reversi.cli

import pt.isel.reversi.core.loadCoreConfig
import pt.isel.reversi.utils.CONFIG_FOLDER
import java.io.File

fun cleanup(func: () -> Unit) {
    val conf = loadCoreConfig()
    File(CONFIG_FOLDER).deleteRecursively()
    File(conf.SAVES_FOLDER).deleteRecursively()
    func()
    File(CONFIG_FOLDER).deleteRecursively()
    File(conf.SAVES_FOLDER).deleteRecursively()
}