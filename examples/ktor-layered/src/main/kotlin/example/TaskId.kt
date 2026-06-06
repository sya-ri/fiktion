package example

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Stable task identifier used inside the domain and exposed by the HTTP API as a UUID string.
 */
@Serializable(with = TaskIdSerializer::class)
@JvmInline
value class TaskId(
    val value: UUID,
) : Comparable<TaskId> {
    override fun compareTo(other: TaskId): Int =
        value.compareTo(other.value)

    override fun toString(): String =
        value.toString()

    companion object {
        /**
         * Parses a task identifier from a UUID string.
         */
        fun parse(value: String): TaskId =
            TaskId(UUID.fromString(value))

        /**
         * Parses a task identifier from a UUID string, returning null when the input is malformed.
         */
        fun parseOrNull(value: String): TaskId? =
            runCatching { parse(value) }.getOrNull()

        /**
         * Creates a new random task identifier.
         */
        fun random(): TaskId =
            TaskId(UUID.randomUUID())
    }
}
