package example

/**
 * Domain model stored by the repository and returned through the service layer.
 */
data class Task(
    val id: TaskId,
    val title: String,
    val description: String?,
    val status: TaskStatus,
)
