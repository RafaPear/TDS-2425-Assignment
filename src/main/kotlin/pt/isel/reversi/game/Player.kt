package pt.isel.reversi.game

import PieceType

data class Player(
    val type: PieceType,
    val points: Int,
    val playsLeft: Int
)