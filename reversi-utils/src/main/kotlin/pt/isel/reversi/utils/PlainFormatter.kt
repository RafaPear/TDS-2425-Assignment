package pt.isel.reversi.utils

class PlainFormatter : java.util.logging.Formatter() {
    override fun format(record: java.util.logging.LogRecord): String {
        return "[${record.level}] (${record.sourceMethodName})" + record.message + System.lineSeparator()
    }
}