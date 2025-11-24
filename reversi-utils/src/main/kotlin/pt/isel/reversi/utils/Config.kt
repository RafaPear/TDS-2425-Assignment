package pt.isel.reversi.utils

/**
 * Represents a configuration with key-value pairs.
 * @property map A map containing configuration key-value pairs.
 * @function getDefaultConfigFileEntries Returns a map of default configuration entries.
 */
interface Config {
    val map: Map<String, String>
    fun getDefaultConfigFileEntries(): Map<String, String>
}