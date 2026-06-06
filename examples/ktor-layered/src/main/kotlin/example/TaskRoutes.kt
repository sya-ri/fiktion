package example

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

/**
 * Registers task HTTP routes.
 */
fun Route.taskRoutes(taskService: TaskService) {
    route("/tasks") {
        get {
            call.respond(taskService.listTasks().map { it.toResponse() })
        }

        get("/{id}") {
            val id = call.parameters["id"]?.let { TaskId.parseOrNull(it) }
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

private fun Task.toResponse(): TaskResponse =
    TaskResponse(
        id = id,
        title = title,
        description = description,
        status = status.name.lowercase(),
    )
