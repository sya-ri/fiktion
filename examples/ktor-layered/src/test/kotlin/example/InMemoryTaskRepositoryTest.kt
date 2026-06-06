package example

import dev.s7a.fiktion.Fiktion
import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import dev.s7a.fiktion.generatesBy
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryTaskRepositoryTest {
    private val fiktion =
        Fiktion {
            Task::title generatesBy { "Task $index" }
        }

    @Test
    fun listsTasksInStableIdOrder() =
        runBlocking {
            val later =
                fiktion.fake<Task> {
                    Task::id generates taskId("00000000-0000-0000-0000-000000000202")
                }
            val earlier =
                fiktion.fake<Task> {
                    Task::id generates taskId("00000000-0000-0000-0000-000000000201")
                }
            val repository = InMemoryTaskRepository(listOf(later, earlier))

            assertEquals(
                listOf(
                    taskId("00000000-0000-0000-0000-000000000201"),
                    taskId("00000000-0000-0000-0000-000000000202"),
                ),
                repository.findAll().map { it.id },
            )
        }
}
