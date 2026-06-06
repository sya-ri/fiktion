package example

interface TaskRepository {
    suspend fun findAll(): List<Task>

    suspend fun findById(id: String): Task?

    suspend fun save(task: Task): Task
}

class InMemoryTaskRepository(
    initialTasks: Iterable<Task> = emptyList(),
) : TaskRepository {
    private val tasks = initialTasks.associateBy { it.id }.toMutableMap()

    override suspend fun findAll(): List<Task> =
        tasks.values.sortedBy { it.id }

    override suspend fun findById(id: String): Task? =
        tasks[id]

    override suspend fun save(task: Task): Task {
        tasks[task.id] = task
        return task
    }
}
