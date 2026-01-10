package pt.isel.reversi.utils

import java.util.logging.Level
import java.util.logging.Logger

/**
 * Environment constants and configuration for the Reversi application.
 *
 * This module defines paths to configuration files and provides a global logger instance
 * configured for consistent logging across all modules.
 */

/** Base data directory for configuration and logs. */
const val BASE_FOLDER = "data"

/** Configuration directory path containing all application configuration files. */
const val CONFIG_FOLDER = "$BASE_FOLDER/config"

/** Core module configuration file path (reversi-core.properties). */
const val CORE_CONFIG_FILE = "$CONFIG_FOLDER/reversi-core.properties"

/** CLI module configuration file path (reversi-cli.properties). */
const val CLI_CONFIG_FILE = "$CONFIG_FOLDER/reversi-cli.properties"

/** Desktop app module configuration file path (reversi-app.properties). */
const val APP_CONFIG_FILE = "$CONFIG_FOLDER/reversi-app.properties"

/**
 * Global logger instance configured with console handler and plain formatter.
 *
 * This logger is configured to output to standard output with a plain text format.
 * It is used throughout the application for logging game events, errors, and diagnostics.
 *
 * Configuration:
 * - Level: ALL (captures all log levels)
 * - Handler: StdOutConsoleHandler (writes to standard output)
 * - Formatter: PlainFormatter (outputs messages in plain text format)
 */
val LOGGER: Logger = Logger.getGlobal().apply {
    useParentHandlers = false
    level = Level.ALL
    val handler = StdOutConsoleHandler().apply {
        level = Level.ALL
        formatter = PlainFormatter()
    }
    addHandler(handler)
}

