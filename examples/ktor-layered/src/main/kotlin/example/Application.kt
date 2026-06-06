package example

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing

/**
 * Starts the sample server on port 8080.
 */
fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}

/**
 * Installs Ktor plugins and wires routes with manually supplied dependencies.
 */
fun Application.module(dependencies: AppDependencies = defaultDependencies()) {
    install(ContentNegotiation) {
        json()
    }

    routing {
        taskRoutes(dependencies.taskService)
    }
}

/**
 * Builds production dependencies for the sample application.
 */
fun defaultDependencies(): AppDependencies {
    val repository = InMemoryTaskRepository()
    return AppDependencies(
        taskService = TaskService(
            idGenerator = RandomTaskIdGenerator(),
            repository = repository,
        ),
    )
}
