package pt.isel.reversi.utils

import java.io.File
import java.time.LocalDate

fun makePathString(vararg parts: String): String =
    listOf(GAME_BASE_FOLDER, *parts).joinToString(separator = "/")

fun setLoggerFilePath(){
    val date = LocalDate.now()
    var name = "${BASE_LOG_FILE_NAME}-$date.log"
    var count = 1
    while (File(name).exists()) {
        name = "${BASE_LOG_FILE_NAME}-$date-${count}.log"
        count++
    }
    File(name).parentFile?.mkdirs()
    File(name).createNewFile()
    val logFileHandler = java.util.logging.FileHandler(name, true).also {
        it.formatter = PlainFormatter()
    }
    LOGGER.addHandler(logFileHandler)
    LOGGER.info("Logging to file '$name' enabled.")
}

fun loadResource(path: String): File {
    val classloader = Thread.currentThread().getContextClassLoader()
    val resource = classloader.getResource(path)
        ?: throw IllegalArgumentException("Resource '$path' not found")
    return File(resource.toURI())
}