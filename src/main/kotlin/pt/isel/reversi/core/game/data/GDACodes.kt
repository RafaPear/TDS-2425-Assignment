package pt.isel.reversi.core.game.data

/**
 * Standard result codes for game data access operations. Each code pairs a numeric value with a
 * short human-readable description and can be used to quickly build a [GDAResult].
 *
 * Use the enum entry as a function (e.g. `GameDataAccessCodes.SUCCESS("Saved", data)`) to produce
 * a typed result object. All non-success codes represent error or exceptional conditions that can
 * still carry a message and optional payload.
 */
@Suppress("unused")
enum class GDACodes() {
    /** Operation completed without errors. */
    SUCCESS,

    /** Referenced entity or expected data was not found. */
    DATA_NOT_FOUND,

    /** Low-level I/O error while reading or writing persistence medium. */
    IO_ERROR,

    /** Malformed or unexpected serialized representation. */
    INVALID_FORMAT,

    /** Fallback code for uncategorized errors. */
    UNKNOWN_ERROR,

    /** Inconsistent state regarding available pieces list. */
    AVAILABLE_PIECES_ERROR,

    /** Inconsistent or invalid side (board size / turn) information. */
    SIDE_ERROR,

    /** Inconsistent or invalid board state (pieces, positions). */
    BOARD_ERROR;

    /**
     * Builds a [GDAResult] with this code.
     *
     * @param message Optional detail about the outcome.
     * @param data Optional payload to return on success (or occasionally on error for context).
     */
    operator fun <T> invoke(message: String? = null, data: T? = null) =
        GDAResult(this, message, data)

    /**
     * Alternative named builder for a [GDAResult] with this code.
     * Prefer the invoke operator for brevity when appropriate.
     */
    fun <T> buildDataAccessResult(message: String? = null, data: T? = null) =
        GDAResult(this, message, data)
}