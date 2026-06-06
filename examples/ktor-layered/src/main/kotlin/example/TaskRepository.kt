package example

/**
 * Persistence boundary for task data.
 */
interface TaskRepository {
    /**
     * Returns all saved tasks.
     */
    suspend fun findAll(): List<Task>

    /**
     * Finds one task by its identifier.
     */
    suspend fun findById(id: TaskId): Task?

    /**
     * Inserts or replaces a task.
     */
    suspend fun save(task: Task): Task
}

/**
 * Simple in-memory repository used by the sample application and tests.
 */
class InMemoryTaskRepository(
    initialTasks: Iterable<Task> = emptyList(),
) : TaskRepository {
    private val tasks = initialTasks.associateBy { it.id }.toMutableMap()

    override suspend fun findAll(): List<Task> =
        tasks.values.sortedBy { it.id }

    override suspend fun findById(id: TaskId): Task? =
        tasks[id]

    override suspend fun save(task: Task): Task {
        tasks[task.id] = task
        return task
    }
}
