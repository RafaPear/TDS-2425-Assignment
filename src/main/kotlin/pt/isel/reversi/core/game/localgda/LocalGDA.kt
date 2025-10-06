package pt.isel.reversi.core.game.localgda

import pt.isel.reversi.core.board.Board
import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.game.GameImpl
import pt.isel.reversi.core.game.data.GDACodes
import pt.isel.reversi.core.game.data.GDAImpl
import pt.isel.reversi.core.game.data.GDAResult
import java.io.File

/**
 * Implementação local de [pt.isel.reversi.core.game.data.GDAImpl] que persiste jogos no sistema de ficheiros
 * em formato texto simples.
 *
 * O formato do ficheiro é o seguinte:
 *  availablePieces: @|#
 *  side: 8
 *  player: symbol points playsLeft
 *  piece: row col symbol
 *  pass: symbol
 */
class LocalGDA : GDAImpl {

    private val availablePiecesPrefix = "availablePieces:"
    private val sidePrefix = "side:"
    private val piecePrefix = "piece:"
    private val passPrefix = "pass:"
    private val playerPrefix = "player:"

    private fun getFile(fileName: String): GDAResult<File> {
        val file = File(fileName)
        return if (file.exists() && file.isFile && file.canWrite())
            GDACodes.SUCCESS(LocalGDAMessages.fileReadSuccess(fileName), file)
        else
            GDACodes.DATA_NOT_FOUND(LocalGDAMessages.fileNotFound(fileName), null)
    }

    private fun getLines(fileName: String): GDAResult<List<String>> {
        val result = getFile(fileName)
        val file = result.data ?: return result.toOtherType()
        return try {
            val lines = file.readLines()
            if (lines.isEmpty())
                GDACodes.DATA_NOT_FOUND(LocalGDAMessages.fileEmpty(fileName), null)
            else
                GDACodes.SUCCESS(LocalGDAMessages.fileReadSuccess(fileName), lines)
        } catch (e: Exception) {
            GDACodes.IO_ERROR(LocalGDAMessages.fileReadError(fileName, e), null)
        }
    }

    private fun parsePiece(line: String): Piece? =
        line.removePrefix(piecePrefix).trim().split(" ").takeIf { it.size == 3 }?.let {
            val (r, c, s) = it
            val row = r.toIntOrNull() ?: return null
            val col = c.toIntOrNull() ?: return null
            val type = PieceType.fromSymbol(s.firstOrNull() ?: return null) ?: return null
            Piece(Coordinate(row, col), type)
        }

    private fun parseSide(line: String): Int? =
        line.removePrefix(sidePrefix).trim().toIntOrNull()

    override fun postPiece(fileName: String, piece: Piece) = try {
        val file = getFile(fileName).data ?: return GDACodes.DATA_NOT_FOUND(
            LocalGDAMessages.fileNotFound(fileName),
            false
        )
        file.appendText("$piecePrefix ${piece.coordinate.row} ${piece.coordinate.col} ${piece.value.symbol}\n")
        GDACodes.SUCCESS(LocalGDAMessages.pieceSaved(fileName, piece), true)
    } catch (e: Exception) {
        GDACodes.IO_ERROR(LocalGDAMessages.fileWriteError(fileName, e), false)
    }

    override fun postGame(fileName: String, game: GameImpl) = try {
        val file = getFile(fileName).data ?: File(fileName).apply { createNewFile() }
        val missing = PieceType.entries.map { it.symbol } - game.players.map { it.type.symbol }.toSet()
        val players = game.players.joinToString("\n") { "$playerPrefix ${it.type.symbol} ${it.points} ${it.playsLeft}" }

        file.writeText(
            "$availablePiecesPrefix ${missing.joinToString("|")}\n" +
            "$sidePrefix ${game.board.side}\n" +
            "$players\n"
        )
        game.board.forEach { postPiece(fileName, it) }
        GDACodes.SUCCESS(LocalGDAMessages.gameWritten(fileName), true)
    } catch (e: Exception) {
        GDACodes.IO_ERROR(LocalGDAMessages.fileWriteError(fileName, e), false)
    }

    override fun postPass(fileName: String, pieceType: PieceType): GDAResult<Boolean> {
        val file = getFile(fileName).data ?: return GDACodes.DATA_NOT_FOUND(
            LocalGDAMessages.fileNotFound(fileName),
            false
        )
        file.appendText("$passPrefix ${pieceType.symbol}\n")
        return GDACodes.SUCCESS(LocalGDAMessages.passRecorded(fileName, pieceType), true)
    }

    override fun getBoard(fileName: String): GDAResult<Board> {
        return try {
            val lines = getLines(fileName).data ?: return GDACodes.DATA_NOT_FOUND(
                LocalGDAMessages.fileEmpty(fileName),
                null
            )
            val side = lines.firstNotNullOfOrNull { if (it.startsWith(sidePrefix)) parseSide(it) else null }
                       ?: return GDACodes.SIDE_ERROR(LocalGDAMessages.missingSide(fileName), null)

            if (side !in 4..26 || side % 2 != 0)
                return GDACodes.SIDE_ERROR(LocalGDAMessages.invalidSide(fileName), null)

            val pieces = lines.filter { it.startsWith(piecePrefix) }.mapNotNull { parsePiece(it) }
            GDACodes.SUCCESS(LocalGDAMessages.boardLoaded(fileName), Board(side, pieces))
        } catch (e: Exception) {
            GDACodes.IO_ERROR(LocalGDAMessages.fileReadError(fileName, e), null)
        }
    }

    override fun getAvailablePieces(fileName: String): GDAResult<List<PieceType>> {
        val lines = getLines(fileName).data ?: return GDACodes.DATA_NOT_FOUND(
            LocalGDAMessages.fileEmpty(fileName),
            null
        )
        val line = lines.firstOrNull()?.takeIf { it.startsWith(availablePiecesPrefix) }
                   ?: return GDACodes.AVAILABLE_PIECES_ERROR(LocalGDAMessages.availablePiecesMissing(fileName), null)

        val symbols = line.removePrefix(availablePiecesPrefix).trim().split("|").filter { it.isNotEmpty() }
        val pieces = symbols.mapNotNull { PieceType.fromSymbol(it.first()) }
        return GDACodes.SUCCESS(LocalGDAMessages.fileReadSuccess(fileName), pieces)
    }

    override fun getLatestPiece(fileName: String): GDAResult<Piece?> {
        val lines = getLines(fileName).data ?: return GDACodes.DATA_NOT_FOUND(
            LocalGDAMessages.fileEmpty(fileName),
            null
        )
        val line = lines.asReversed().firstOrNull { it.startsWith(piecePrefix) }
                   ?: return GDACodes.SUCCESS(LocalGDAMessages.noPiecesFound(fileName), null)

        val piece = parsePiece(line)
                    ?: return GDACodes.BOARD_ERROR(LocalGDAMessages.invalidPiece(), null)

        return GDACodes.SUCCESS(LocalGDAMessages.latestPieceFound(fileName, piece), piece)
    }
}
