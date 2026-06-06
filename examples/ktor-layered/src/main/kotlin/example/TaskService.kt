package example

/**
 * Application service that owns task use cases.
 */
class TaskService(
    private val idGenerator: TaskIdGenerator,
    private val repository: TaskRepository,
) {
    /**
     * Lists tasks in repository order.
     */
    suspend fun listTasks(): List<Task> =
        repository.findAll()

    /**
     * Finds one task by id.
     */
    suspend fun getTask(id: TaskId): Task? =
        repository.findById(id)

    /**
     * Creates an open task from a service-layer command.
     */
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
