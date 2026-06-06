package example

import dev.s7a.fiktion.fake
import dev.s7a.fiktion.generates
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TaskRoutesTest {
    @Test
    fun returnsTasksFromInjectedDependencies() =
        testApplication {
            val existingTask =
                fake<Task>(seed = 20) {
                    Task::id generates "task-existing"
                    Task::title generates "Document route tests"
                    Task::description generates "Use Fiktion for route fixtures"
                    Task::status generates TaskStatus.Completed
                }
            application {
                module(
                    AppDependencies(
                        taskService =
                            TaskService(
                                idGenerator = TaskIdGenerator { "unused" },
                                repository = InMemoryTaskRepository(listOf(existingTask)),
                            ),
                    ),
                )
            }

            val response = client.get("/tasks/task-existing")

            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(
                TaskResponse(
                    id = "task-existing",
                    title = "Document route tests",
                    description = "Use Fiktion for route fixtures",
                    status = "completed",
                ),
                Json.decodeFromString<TaskResponse>(response.bodyAsText()),
            )
        }

    @Test
    fun createsTasksThroughHttpLayer() =
        testApplication {
            application {
                module(
                    AppDependencies(
                        taskService =
                            TaskService(
                                idGenerator = TaskIdGenerator { "task-from-route" },
                                repository = InMemoryTaskRepository(),
                            ),
                    ),
                )
            }

            val response =
                client.post("/tasks") {
                    contentType(ContentType.Application.Json)
                    setBody("""{"title":"Build layered sample","description":"Keep DI manual"}""")
                }

            assertEquals(HttpStatusCode.Created, response.status)
            assertEquals(
                TaskResponse(
                    id = "task-from-route",
                    title = "Build layered sample",
                    description = "Keep DI manual",
                    status = "open",
                ),
                Json.decodeFromString<TaskResponse>(response.bodyAsText()),
            )
        }
}
