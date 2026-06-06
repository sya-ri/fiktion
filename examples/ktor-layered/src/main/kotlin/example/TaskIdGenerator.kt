package example

/**
 * Supplies task identifiers so tests can inject deterministic IDs without replacing the service.
 */
fun interface TaskIdGenerator {
    /**
     * Returns the next identifier for a task being created.
     */
    fun nextId(): TaskId
}
