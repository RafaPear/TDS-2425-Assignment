package pt.isel.reversi.utils.audio

data class AudioModifier(
    val closeOnFinish: Boolean = false,
    val gotoStartOnStop: Boolean = true,
    val startPosition: Int? = null,
    val loop: Boolean = false,
    val loopStartPosition: Int? = null,
    val loopEndPosition: Int? = null,
) {
    fun setToCloseOnFinish(): AudioModifier =
        this.copy(closeOnFinish = true)

    fun setToNotGoToStartOnStop(): AudioModifier =
        this.copy(gotoStartOnStop = false)

    fun setStartPosition(position: Int): AudioModifier =
        this.copy(startPosition = position)

    fun setToLoop(startPosition: Int? = null, endPosition: Int? = null): AudioModifier =
        this.copy(loop = true, loopStartPosition = startPosition, loopEndPosition = endPosition)

    fun setToLoop(): AudioModifier = this.copy(loop = true, loopStartPosition = 0, loopEndPosition = -1)
}