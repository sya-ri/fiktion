package example

import kotlinx.serialization.Serializable

/**
 * JSON response body for task resources.
 */
@Serializable
data class TaskResponse(
    val id: TaskId,
    val title: String,
    val description: String?,
    val status: String,
)
