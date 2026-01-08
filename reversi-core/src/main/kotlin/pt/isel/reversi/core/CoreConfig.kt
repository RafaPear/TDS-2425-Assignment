package pt.isel.reversi.core

import pt.isel.reversi.core.storage.GameStorageType
import pt.isel.reversi.utils.Config
import pt.isel.reversi.utils.makePathString

/**
 * Configuration holder for core game parameters loaded from properties files.
 * Manages board dimensions, character representations, and storage configuration.
 *
 * @property map The underlying configuration map with string keys and values.
 */
class CoreConfig(override val map: Map<String, String>) : Config {
    /** Side length of the game board. */
    val BOARD_SIDE = map["BOARD_SIDE"]?.toIntOrNull() ?: 8

    /** Character representing the target pieces on the board. */
    val TARGET_CHAR = map["TARGET_CHAR"]?.firstOrNull() ?: '*'

    /** Character representing empty spaces on the board. */
    val EMPTY_CHAR = map["EMPTY_CHAR"]?.firstOrNull() ?: '.'

    /** Configured storage type for game state persistence. */
    val STORAGE_TYPE = GameStorageType.fromConfigValue(map["STORAGE_TYPE"].toString())

    /** Directory path where game saves are stored. */
    val SAVES_PATH = map["SAVES_PATH"] ?: makePathString("saves")

    /** Name of the MongoDB database for storing game states. */
    val DATABASE_NAME = map["DATABASE_NAME"] ?: "gameSaves"

    /** URI for connecting to the MongoDB server. */
    val MONGO_URI = map["MONGO_URI"] ?: "comunity.ddns.net"

    /** Name of the MongoDB collection for storing game states. */
    val MONGO_PORT = map["MONGO_PORT"]?.toIntOrNull() ?: 27017

    /** Username for MongoDB authentication. */
    val MONGO_USER = map["MONGO_USER"] ?: "<reversiUser>"

    /** Password for MongoDB authentication. */
    val MONGO_PASSWORD = map["MONGO_PASSWORD"] ?: "<reversiPass>"

    override fun getDefaultConfigFileEntries(): Map<String, String> {
        return mapOf(
            "BOARD_SIDE" to BOARD_SIDE.toString(),
            "TARGET_CHAR" to TARGET_CHAR.toString(),
            "EMPTY_CHAR" to EMPTY_CHAR.toString(),
            "SAVES_PATH" to SAVES_PATH,
            "STORAGE_TYPE" to GameStorageType.FILE_STORAGE.name,
            "MONGO_URI" to MONGO_URI,
            "MONGO_USER" to MONGO_USER,
            "MONGO_PASSWORD" to MONGO_PASSWORD,
            "MONGO_PORT" to MONGO_PORT.toString(),
            "DATABASE_NAME" to DATABASE_NAME,
        )
    }
}