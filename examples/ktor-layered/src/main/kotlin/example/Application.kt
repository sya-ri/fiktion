package example

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module(dependencies: AppDependencies = defaultDependencies()) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        taskRoutes(dependencies.taskService)
    }
}

data class AppDependencies(
    val taskService: TaskService,
)

fun defaultDependencies(): AppDependencies {
    val repository = InMemoryTaskRepository()
    return AppDependencies(
        taskService = TaskService(
            idGenerator = RandomTaskIdGenerator(),
            repository = repository,
        ),
    )
}
