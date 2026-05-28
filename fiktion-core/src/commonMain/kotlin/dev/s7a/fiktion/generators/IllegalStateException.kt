package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an illegal state exception.
 */
public fun FakeContext.illegalStateException(message: String = string()): IllegalStateException = IllegalStateException(message)
