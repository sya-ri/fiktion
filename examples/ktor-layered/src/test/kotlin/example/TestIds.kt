package example

import java.util.UUID

internal fun taskId(value: String): TaskId =
    TaskId(UUID.fromString(value))
