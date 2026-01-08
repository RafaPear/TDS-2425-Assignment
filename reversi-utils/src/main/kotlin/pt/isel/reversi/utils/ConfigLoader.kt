package pt.isel.reversi.utils

import java.io.File
import java.util.*

/**
 * Configuration loader that reads properties files and creates Config instances.
 * Automatically creates default configuration files if they don't exist.
 *
 * @param U The Config subclass type to instantiate with loaded values.
 * @property path File path to the configuration properties file.
 * @property factory Function to construct the Config instance from a key-value map.
 */
class ConfigLoader<U : Config>(
    val path: String,
    val factory: (Map<String, String>) -> U
) {

    fun loadConfig(): U {
        val file = File(path)
        val props = Properties()

        if (!file.exists()) {
            file.parentFile?.mkdirs()
            file.createNewFile()
        } else {
            file.inputStream().use(props::load)
        }

        val defaults = factory(emptyMap()).getDefaultConfigFileEntries()

        var changed = false
        for ((key, value) in defaults) {
            if (!props.containsKey(key)) {
                props.setProperty(key, value)
                changed = true
            }
        }

        if (changed) {
            file.outputStream().use {
                props.store(it, "Configuration file at ${file.absolutePath}")
            }
        }

        val configMap = props.entries.associate {
            it.key.toString() to it.value.toString().trim()
        }

        return factory(configMap)
    }
}
