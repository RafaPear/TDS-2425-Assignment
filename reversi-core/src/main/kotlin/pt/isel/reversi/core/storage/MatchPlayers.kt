package pt.isel.reversi.core.storage

import pt.isel.reversi.core.Player
import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.PieceType

data class MatchPlayers(val player1: Player? = null, val player2: Player? = null): Iterable<Player> {
    init {
        if (player1 != null && player2 != null) {
            require(player1.type != player2.type) { "Players must have different piece types" }
        }
    }

    fun isEmpty(): Boolean = player1 == null && player2 == null

    fun isNotEmpty(): Boolean = !isEmpty()

    fun isFull(): Boolean = player1 != null && player2 != null

    fun hasOnlyOnePlayer(): Boolean = (player1 == null) != (player2 == null)

    fun getPlayerByType(type: PieceType): Player? =
        when (type) {
            PieceType.BLACK -> player1?.takeIf { it.type == PieceType.BLACK }
            PieceType.WHITE -> player2?.takeIf { it.type == PieceType.WHITE }
        }

    fun refreshPlayers(board: Board): MatchPlayers {
        val refreshedPlayer1 = player1?.refresh(board)
        val refreshedPlayer2 = player2?.refresh(board)
        return MatchPlayers(refreshedPlayer1, refreshedPlayer2)
    }

    fun getFreeType(): PieceType? =
        when {
            player1 == null && player2 == null -> PieceType.BLACK
            player1 != null && player2 == null -> player1.type.swap()
            player1 == null && player2 != null -> player2.type.swap()
            else -> null
        }

    fun getFirstPlayer(): Player? =
        when {
            player1 != null -> player1
            player2 != null -> player2
            else -> null
        }

    fun addPlayerOrNull(newPlayerName: Player) = when {
        this.isFull() -> null
        this.getPlayerByType(newPlayerName.type) != null  -> null
        this.player1 == null -> MatchPlayers(newPlayerName, this.player2)
        this.player2 == null -> MatchPlayers(this.player1, newPlayerName)
        else -> null
    }

    override fun iterator(): Iterator<Player> {
        val playersList = listOfNotNull(player1, player2)
        return playersList.iterator()
    }
}