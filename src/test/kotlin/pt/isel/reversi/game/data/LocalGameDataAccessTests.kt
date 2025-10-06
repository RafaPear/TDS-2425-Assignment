package pt.isel.reversi.game.data

import pt.isel.reversi.core.board.Coordinate
import pt.isel.reversi.core.board.Piece
import pt.isel.reversi.core.board.PieceType
import pt.isel.reversi.core.game.data.GDACodes
import pt.isel.reversi.core.game.localgda.LocalGDA
import java.nio.file.Files
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocalGameDataAccessTests {

    private fun tempFilePath(name: String = "game.txt"): Path {
        val dir = Files.createTempDirectory("reversi-test-")
        return dir.resolve(name)
    }

    @Test
    fun postGame_writes_headers_with_players_and_side() {
        val path = tempFilePath()
        val dataAccess = LocalGDA()
        val game = MockGame.EmptyPlayers(dataAccess, path.fileName.toString())

        val result = dataAccess.postGame(path.toString(), game)
        assertEquals(GDACodes.SUCCESS, result.code)
        assertTrue(Files.exists(path))
        val lines = Files.readAllLines(path)
        // Expect two header lines
        assertEquals(2, lines.size)
        assertTrue(lines[0].startsWith("availablePieces:"))
        assertTrue(lines[0].contains("@|#") || lines[0].contains("#|@")) // order currently as in list
        assertEquals("side: 8", lines[1])
    }

    @Test
    fun postGame_overwrites_existing_file() {
        val path = tempFilePath()
        Files.writeString(path, "junk line that should be overwritten")
        val dataAccess = LocalGDA()
        val game = MockGame.EmptyPlayers(dataAccess, path.fileName.toString())
        val gameToReplace = MockGame.TwoPlayers(dataAccess, path.fileName.toString())

        val result = dataAccess.postGame(path.toString(), game)
        assertEquals(GDACodes.SUCCESS, result.code)
        val resultReplace = dataAccess.postGame(path.toString(), gameToReplace)
        assertEquals(GDACodes.SUCCESS, resultReplace.code)
        val lines = Files.readAllLines(path)
        assertEquals(2, lines.size)
        assertEquals("availablePieces:", lines[0].trim())
        assertEquals("side: 8", lines[1])
    }

    @Test
    fun postPiece_fails_when_file_missing() {
        val path = tempFilePath() // directory created, file not yet created
        val dataAccess = LocalGDA()
        val piece = Piece(Coordinate(1, 1), PieceType.BLACK)
        val result = dataAccess.postPiece(path.toString(), piece)
        // Implementation returns DATA_NOT_FOUND when file doesn't exist
        assertEquals(GDACodes.DATA_NOT_FOUND, result.code)
    }

    @Test
    fun postPiece_succeeds_with_empty_available_pieces_line() {
        val path = tempFilePath()
        val dataAccess = LocalGDA()
        val game = MockGame.TwoPlayers(dataAccess, path.fileName.toString())
        assertEquals(GDACodes.SUCCESS, dataAccess.postGame(path.toString(), game).code)

        val result = dataAccess.postPiece(path.toString(), Piece(Coordinate(1, 1), PieceType.BLACK))
        assertEquals(GDACodes.SUCCESS, result.code)
        val lines = Files.readAllLines(path)
        assertEquals(3, lines.size) // 2 headers + 1 piece
        assertTrue(lines[2].startsWith("piece:"))
    }

    @Test
    fun postPass_after_piece_of_other_player_succeeds() {
        val path = tempFilePath()
        val dataAccess = LocalGDA()
        val game = MockGame.TwoPlayers(dataAccess, path.fileName.toString())
        dataAccess.postGame(path.toString(), game)

        // First piece by BLACK
        assertEquals(
            GDACodes.SUCCESS,
            dataAccess.postPiece(path.toString(), Piece(Coordinate(1, 1), PieceType.BLACK)).code
        )
        // Pass by WHITE should be allowed (last piece BLACK)
        val pass = dataAccess.postPass(path.toString(), PieceType.WHITE)
        assertEquals(GDACodes.SUCCESS, pass.code)
    }
}