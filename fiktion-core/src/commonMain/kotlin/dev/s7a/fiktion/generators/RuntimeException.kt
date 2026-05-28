package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a runtime exception.
 */
public fun FakeContext.runtimeException(message: String = string()): RuntimeException = RuntimeException(message)
