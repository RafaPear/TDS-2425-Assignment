package pt.isel.reversi.utils.audio

import pt.isel.reversi.utils.LOGGER
import pt.isel.reversi.utils.audio.controls.BooleanControlWrapper
import pt.isel.reversi.utils.audio.controls.FloatControlWrapper
import java.net.URL
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

data class AudioWrapper(
    val id: String,
    val clip: Clip,
    val modifier: AudioModifier = AudioModifier()
) {
    val masterGainControl = FloatControlWrapper.MasterVolumeControl(clip)
    val balanceControl = FloatControlWrapper.BalanceControl(clip)
    val muteControl = BooleanControlWrapper.MuteControl(clip)

    val loopStart = modifier.loopStartPosition?.coerceIn(0, clip.frameLength - 1) ?: 0
    val loopEnd = modifier.loopEndPosition?.coerceIn(-1, clip.frameLength - 1) ?: -1

    fun isPlaying(): Boolean = clip.isRunning

    init {
        clip.framePosition = modifier.startPosition?.coerceIn(0, clip.frameLength - 1) ?: 0
        LOGGER.info("AUDIO '${id}' initialized")
    }

    fun play() {
        if (!clip.isRunning) {
            if (modifier.loop) {
                clip.setLoopPoints(loopStart, loopEnd)
                clip.loop(Clip.LOOP_CONTINUOUSLY)
            }
            else clip.start()
        }
    }

    fun pause() {
        if (clip.isRunning) clip.stop()
    }

    fun stop() {
        clip.stop()
        if (modifier.gotoStartOnStop) clip.framePosition = 0
        if (modifier.closeOnFinish) clip.close()
    }

    companion object {
        fun loadAudio(name: String, url: URL, modifier: AudioModifier = AudioModifier()): AudioWrapper {
            val original = AudioSystem.getAudioInputStream(url)

            val baseFormat = original.format
            val decodedFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.sampleRate,
                16,
                baseFormat.channels,
                baseFormat.channels * 2,
                baseFormat.sampleRate,
                false
            )

            val decoded = AudioSystem.getAudioInputStream(decodedFormat, original)

            val clip = AudioSystem.getClip()
            clip.open(decoded)

            return AudioWrapper(name, clip, modifier)
        }
    }
}