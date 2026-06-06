package example

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable

fun Route.taskRoutes(taskService: TaskService) {
    route("/tasks") {
        get {
            call.respond(taskService.listTasks().map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]
            val task = id?.let { taskService.getTask(it) }

            if (task == null) {
                call.respond(HttpStatusCode.NotFound)
            } else {
                call.respond(task.toResponse())
            }
        }

        post {
            val request = call.receive<CreateTaskRequest>()
            val task =
                taskService.createTask(
                    CreateTaskCommand(
                        title = request.title,
                        description = request.description,
                    ),
                )

            call.respond(HttpStatusCode.Created, task.toResponse())
        }
    }
}

@Serializable
data class CreateTaskRequest(
    val title: String,
    val description: String? = null,
)

@Serializable
data class TaskResponse(
    val id: String,
    val title: String,
    val description: String?,
    val status: String,
)

private fun Task.toResponse(): TaskResponse =
    TaskResponse(
        id = id,
        title = title,
        description = description,
        status = status.name.lowercase(),
    )
