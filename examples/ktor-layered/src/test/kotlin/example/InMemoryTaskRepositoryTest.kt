package example

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generatesBy
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryTaskRepositoryTest {
    private val fiktion =
        Fiktion {
            name<String>("id") generatesBy { "task-$index" }
        }

    @Test
    fun listsTasksInStableIdOrder() =
        runBlocking {
            val later = fiktion.fake<Task>(seed = 1)
            val earlier = fiktion.fake<Task>(seed = 2)
            val repository = InMemoryTaskRepository(listOf(later.copy(id = "task-2"), earlier.copy(id = "task-1")))

            assertEquals(
                listOf("task-1", "task-2"),
                repository.findAll().map { it.id },
            )
        }
}
