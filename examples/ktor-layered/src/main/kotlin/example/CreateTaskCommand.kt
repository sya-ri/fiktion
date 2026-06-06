package example

/**
 * Service-layer command for creating a task from API input.
 */
data class CreateTaskCommand(
    val title: String,
    val description: String?,
)
