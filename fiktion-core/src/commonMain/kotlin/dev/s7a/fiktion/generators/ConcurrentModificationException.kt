package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a concurrent modification exception.
 */
public fun FakeContext.concurrentModificationException(message: String = string()): ConcurrentModificationException =
    ConcurrentModificationException(message)
