package pt.isel.reversi.app.gameAudio

import pt.isel.reversi.app.AppTheme
import pt.isel.reversi.utils.LOGGER
import pt.isel.reversi.utils.audio.AudioModifier
import pt.isel.reversi.utils.audio.AudioPool
import pt.isel.reversi.utils.audio.AudioPool.Companion.buildAudioPool
import pt.isel.reversi.utils.audio.AudioWrapper.Companion.loadAudio
import pt.isel.reversi.utils.loadResourcesFromFolder


/**
 * Loads the game's audio pool from the resources.
 * @return An AudioPool containing all loaded audio tracks.
 */
fun loadGameAudioPool(theme: AppTheme, mainFolder: String = "audios/"): AudioPool {
    val audioNames = listOf(
        theme.backgroundMusic,
        theme.gameMusic,
        theme.placePieceSound
    )
    val audioPaths = loadResourcesFromFolder(mainFolder) { fileName, url ->
        val name = fileName.substringBeforeLast('.')
        try {
            when (name) {
                in setOf(theme.backgroundMusic, theme.gameMusic) -> loadAudio(
                    name,
                    url,
                    AudioModifier().setToLoopInfinitely()
                )

                in audioNames -> loadAudio(name, url.toURI().toURL())
                else -> null
            }
        } catch (e: Exception) {
            LOGGER.warning("Failed to load audio $name: ${e.message}")
            null
        }
    }

    return buildAudioPool { for (audio in audioPaths) add(audio) }
}