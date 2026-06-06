package example

/**
 * Production id generator backed by random UUIDs.
 */
class RandomTaskIdGenerator : TaskIdGenerator {
    override fun nextId(): TaskId =
        TaskId.random()
}
