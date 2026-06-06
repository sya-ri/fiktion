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
                    idGenerator = TaskIdGenerator { "task-created" },
                    repository = repository,
                )
            val command =
                fake<CreateTaskCommand>(seed = 10) {
                    CreateTaskCommand::title generates "Write the Ktor sample"
                    CreateTaskCommand::description generates "Show service-layer tests with Fiktion"
                }

            val task = service.createTask(command)

            assertEquals("task-created", task.id)
            assertEquals("Write the Ktor sample", task.title)
            assertEquals("Show service-layer tests with Fiktion", task.description)
            assertEquals(TaskStatus.Open, task.status)
            assertEquals(task, repository.findById("task-created"))
        }
}
