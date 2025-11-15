package pt.isel.reversi.core

import pt.isel.reversi.core.storage.GameStorageType
import pt.isel.reversi.utils.Config
import pt.isel.reversi.utils.makePathString

class CoreConfig(override val map: Map<String, String>) : Config {
    /** Side length of the game board. */
    val BOARD_SIDE = map["BOARD_SIDE"]?.toIntOrNull() ?: 8

    /** Character representing the target pieces on the board. */
    val TARGET_CHAR = map["TARGET_CHAR"]?.firstOrNull() ?: '*'

    /** Character representing empty spaces on the board. */
    val EMPTY_CHAR = map["EMPTY_CHAR"]?.firstOrNull() ?: '.'

    val SAVES_FOLDER = map["SAVES_FOLDER"] ?: makePathString("saves")

    val STORAGE_TYPE = GameStorageType.fromConfigValue(map["STORAGE_TYPE"].toString())

    override fun getDefaultConfigFileEntries(): Map<String, String> {
        return mapOf(
            "BOARD_SIDE" to BOARD_SIDE.toString(),
            "TARGET_CHAR" to TARGET_CHAR.toString(),
            "EMPTY_CHAR" to EMPTY_CHAR.toString(),
            "SAVES_FOLDER" to SAVES_FOLDER,
            "STORAGE_TYPE" to GameStorageType.FILE_STORAGE.name,
        )
    }
}