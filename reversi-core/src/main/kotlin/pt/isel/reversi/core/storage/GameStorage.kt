package pt.isel.reversi.core.storage

import okio.FileSystem
import okio.Path.Companion.toPath
import pt.isel.reversi.core.storage.serializers.GameFileSerializer
import pt.isel.reversi.storage.Serializer
import pt.isel.reversi.storage.Storage

class GameStorage() : Storage<String, GameFile, String> {
    override val serializer: Serializer<GameFile, String> = GameFileSerializer()
    override fun new(id: String): GameFile {
        val fs = FileSystem.SYSTEM

        require(!fs.exists(id.toPath())) { "There is already an entity with given id '$id'" }

        fs.write(id.toPath(), true) {
            // create empty file
        }

        return GameFile().setId(id)
    }

    override fun load(id: String): GameFile? {
        val fs = FileSystem.SYSTEM

        if (!fs.exists(id.toPath())) return null

        val content = fs.read(id.toPath()) { readUtf8() }
        return serializer.deserialize(content).setId(id)
    }

    override fun save(id: String, obj: GameFile) {
        val fs = FileSystem.SYSTEM

        require(fs.exists(id.toPath())) { "There is no entity with given id '$id'" }

        val objStr = serializer.serialize(obj)

        fs.write(id.toPath(), false) {
            writeUtf8(objStr)
        }
    }

    override fun delete(id: String) {
        val fs = FileSystem.SYSTEM

        require(fs.exists(id.toPath())) { "There is no entity with given id '$id'" }

        fs.delete(id.toPath())
    }
}