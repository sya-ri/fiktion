package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an illegal argument exception.
 */
public fun FakeContext.illegalArgumentException(message: String = string()): IllegalArgumentException = IllegalArgumentException(message)
