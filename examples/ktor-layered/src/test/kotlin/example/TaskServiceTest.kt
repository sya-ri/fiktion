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
                    idGenerator = TaskIdGenerator { taskId("00000000-0000-0000-0000-000000000101") },
                    repository = repository,
                )
            val command =
                fake<CreateTaskCommand> {
                    CreateTaskCommand::title generates "Write the Ktor sample"
                    CreateTaskCommand::description generates "Show service-layer tests with Fiktion"
                }

            val task = service.createTask(command)

            assertEquals(taskId("00000000-0000-0000-0000-000000000101"), task.id)
            assertEquals("Write the Ktor sample", task.title)
            assertEquals("Show service-layer tests with Fiktion", task.description)
            assertEquals(TaskStatus.Open, task.status)
            assertEquals(task, repository.findById(taskId("00000000-0000-0000-0000-000000000101")))
        }
}
