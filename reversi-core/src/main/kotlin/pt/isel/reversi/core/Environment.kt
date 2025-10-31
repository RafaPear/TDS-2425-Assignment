package pt.isel.reversi.core

import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.storage.GameState
import pt.isel.reversi.core.storage.serializers.GameStateSerializer
import pt.isel.reversi.storage.FileStorage

const val BOARD_SIDE = 8
val First_Player_TURN = PieceType.BLACK

val FILE_DATA_ACCESS = FileStorage(
    folder = "saves",
    factory = { id -> GameState(id = id) },
    serializer = GameStateSerializer()
)
