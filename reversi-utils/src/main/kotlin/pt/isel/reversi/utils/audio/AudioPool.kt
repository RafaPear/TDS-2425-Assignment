package pt.isel.reversi.utils.audio

import pt.isel.reversi.utils.LOGGER
import kotlin.collections.all
import kotlin.collections.find

data class AudioPool(val pool: List<AudioWrapper>) {
    init {
        LOGGER.info("AudioPool created with ${pool.size} audio tracks")
    }

    fun play(id: String) {
        val track = pool.find { it.id == id } ?: return
        track.play()
    }

    fun stop(id: String) {
        val track = pool.find { it.id == id } ?: return
        track.stop()
    }

    fun pause(id: String) {
        val track = pool.find { it.id == id } ?: return
        track.pause()
    }

    fun getAudioTrack(id: String): AudioWrapper? {
        return pool.find { it.id == id }
    }

    fun playAll() { pool.forEach { it.play() } }

    fun stopAll() {
        pool.forEach { it.stop() }
    }

    fun pauseAll() {
        pool.forEach { it.pause() }
    }

    fun destroy() {
        pool.forEach { it.clip.close() }
    }

    fun isPlaying(id: String): Boolean {
        val track = pool.find { it.id == id }
        return track?.isPlaying() ?: false
    }

    fun whileNotFinished(func: () -> Unit = {}) {
        while (pool.any { it.isPlaying() }) {
            func()
        }
    }

    fun whileNotFinished(id: String, func: () -> Unit = {}) {
        val audio = pool.find { it.id == id } ?: return
        while (audio.isPlaying()) {
            func()
        }
    }

    fun changeMasterVolume(volume: Float) {
        pool.forEach { it.masterGainControl.addValue(volume) }
    }

    fun setMasterVolume(volume: Float) {
        pool.forEach { it.masterGainControl.updateValue(volume) }
    }

    fun changeBalance(balance: Float) {
        pool.forEach { it.balanceControl.addValue(balance) }
    }

    fun setBalance(balance: Float) {
        pool.forEach { it.balanceControl.updateValue(balance) }
    }

    fun mute(mute: Boolean) {
        pool.forEach { it.muteControl.updateValue(mute) }
    }

    fun isPoolStopped(): Boolean = pool.all { !it.isPlaying() }

    companion object {
        fun buildAudioPool(builderAction: MutableList<AudioWrapper>.() -> Unit): AudioPool {
            val audioList = mutableListOf<AudioWrapper>()
            audioList.builderAction()
            return AudioPool(audioList)
        }
    }
}
