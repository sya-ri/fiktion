package example

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

/**
 * Serializes task identifiers as JSON UUID strings.
 */
object TaskIdSerializer : KSerializer<TaskId> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("TaskId", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: TaskId,
    ) {
        encoder.encodeString(value.toString())
    }

    override fun deserialize(decoder: Decoder): TaskId =
        TaskId.parse(decoder.decodeString())
}

/**
 * Domain model stored by the repository and returned through the service layer.
 */
data class Task(
    val id: TaskId,
    val title: String,
    val description: String?,
    val status: TaskStatus,
)

/**
 * Lifecycle state for a task.
 */
enum class TaskStatus {
    Open,
    Completed,
}

/**
 * Service-layer command for creating a task from API input.
 */
data class CreateTaskCommand(
    val title: String,
    val description: String?,
)
