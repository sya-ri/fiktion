package example

import kotlinx.serialization.Serializable

/**
 * JSON request body for creating a task.
 */
@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
)
