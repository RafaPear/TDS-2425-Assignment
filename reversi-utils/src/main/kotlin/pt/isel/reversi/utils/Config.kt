package pt.isel.reversi.utils

interface Config {
    val map: Map<String, String>
    fun getDefaultConfigFileEntries(): Map<String, String>
}