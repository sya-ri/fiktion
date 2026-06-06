package example

import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskServiceTest {
    @Test
    fun createsOpenTaskFromCommand() =
        runBlocking {
            val repository = InMemoryTaskRepository()
            val service =
                TaskService(
                    repository = repository,
                )
            val command =
                fake<CreateTaskCommand> {
                    CreateTaskCommand::title generates "Write the Ktor sample"
                    CreateTaskCommand::description generates "Show service-layer tests with Fiktion"
                }

            val task = service.createTask(command)

            assertEquals("Write the Ktor sample", task.title)
            assertEquals("Show service-layer tests with Fiktion", task.description)
            assertEquals(TaskStatus.Open, task.status)
            assertEquals(task, repository.findById(task.id))
        }
}
