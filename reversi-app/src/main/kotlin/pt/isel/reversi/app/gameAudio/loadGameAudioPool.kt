package pt.isel.reversi.app.gameAudio

import pt.isel.reversi.app.BACKGROUND_MUSIC
import pt.isel.reversi.utils.LOGGER
import pt.isel.reversi.utils.audio.AudioModifier
import pt.isel.reversi.utils.audio.AudioPool
import pt.isel.reversi.utils.audio.AudioPool.Companion.buildAudioPool
import pt.isel.reversi.utils.audio.AudioWrapper.Companion.loadAudio
import pt.isel.reversi.utils.loadResource

fun loadGameAudioPool(): AudioPool {
    val audioPaths = loadResource("audios/").listFiles().mapNotNull {
        val name = it.name.substringBeforeLast('.')
        try {
            if (name == BACKGROUND_MUSIC)
                loadAudio(
                    name,
                    it.toURI().toURL(),
                    AudioModifier().setToLoop()
                )
            else loadAudio(name, it.toURI().toURL())
        }
        catch (e: Exception) {
            LOGGER.warning("Failed to load audio $name: ${e.message}")
            null
        }
    }
    return buildAudioPool { for (audio in audioPaths) add(audio) }
}