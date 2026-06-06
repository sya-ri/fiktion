package example

import java.util.UUID

class TaskService(
    private val idGenerator: TaskIdGenerator,
    private val repository: TaskRepository,
) {
    suspend fun listTasks(): List<Task> =
        repository.findAll()

    suspend fun getTask(id: String): Task? =
        repository.findById(id)

    suspend fun createTask(command: CreateTaskCommand): Task {
        val task =
            Task(
                id = idGenerator.nextId(),
                title = command.title,
                description = command.description,
                status = TaskStatus.Open,
            )
        return repository.save(task)
    }
}

fun interface TaskIdGenerator {
    fun nextId(): String
}

class RandomTaskIdGenerator : TaskIdGenerator {
    override fun nextId(): String =
        UUID.randomUUID().toString()
}
