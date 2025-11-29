package pt.isel.reversi.storage

import kotlinx.coroutines.runBlocking
import java.io.File
import kotlin.test.Test
import kotlin.test.assertFails

class AsyncFileStorageTest {
    data class MockData(val id: Int, val name: String)

    class TestSerializer : Serializer<MockData, String> {
        override fun serialize(obj: MockData): String {
            return "${obj.id},${obj.name}"
        }

        override fun deserialize(obj: String): MockData {
            val parts = obj.split(",")
            return MockData(parts[0].toInt(), parts[1])
        }
    }

    val asyncFileStorage = AsyncFileStorage(
        folder = "test-async-saves",
        serializer = TestSerializer()
    )


    fun cleanup(func: () -> Unit) {
        File("test-async-saves").deleteRecursively()
        func()
        File("test-async-saves").deleteRecursively()
    }

    @Test
    fun `Run new at an already existing id fails`() {
        cleanup {
            assertFails {
                runBlocking {
                    asyncFileStorage.new(1.toString()) {
                        MockData(1, "Test1")
                    }
                    asyncFileStorage.new(1.toString()) {
                        MockData(1, "Test1")
                    }
                }
            }
        }
    }

    @Test
    fun `Run save at a non existing id fails`() {
        cleanup {
            assertFails {
                runBlocking {
                    asyncFileStorage.save(1.toString(), MockData(1, "Test1"))
                }
            }
        }
    }

    @Test
    fun `Run load at a non existing id returns null`() {
        cleanup {
            runBlocking {
                val data = asyncFileStorage.load(1.toString())
                assert(data == null)
            }
        }
    }

    @Test
    fun `Run new and load works`() {
        cleanup {
            runBlocking {
                val data1 = asyncFileStorage.new(1.toString()) { MockData(1, "Test1") }
                val data2 = asyncFileStorage.load(1.toString())

                assert(data1 == data2)
            }
        }
    }

    @Test
    fun `Run new, save and load works`() {
        cleanup {
            runBlocking {
                asyncFileStorage.new(1.toString()) {
                    MockData(1, "Test1")
                }
                val updatedData = MockData(1, "UpdatedTest1")
                asyncFileStorage.save(1.toString(), updatedData)
                val data2 = asyncFileStorage.load(1.toString())

                assert(data2 == updatedData)
            }
        }
    }

    @Test
    fun `Run new and delete works`() {
        cleanup {
            runBlocking {
                asyncFileStorage.new(1.toString()) { MockData(1, "Test1") }
                asyncFileStorage.delete(1.toString())
                val data = asyncFileStorage.load(1.toString())
                assert(data == null)
            }
        }
    }
}