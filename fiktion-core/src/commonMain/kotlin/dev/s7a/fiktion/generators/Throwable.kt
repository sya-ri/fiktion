package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a throwable.
 */
public fun FakeContext.throwable(message: String = string()): Throwable = Throwable(message)
