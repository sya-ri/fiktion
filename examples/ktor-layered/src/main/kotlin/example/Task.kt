package example

data class Task(
    val id: String,
    val title: String,
    val description: String?,
    val status: TaskStatus,
)

enum class TaskStatus {
    Open,
    Completed,
}

data class CreateTaskCommand(
    val title: String,
    val description: String?,
)
