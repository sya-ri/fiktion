package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a null pointer exception.
 */
public fun FakeContext.nullPointerException(message: String = string()): NullPointerException = NullPointerException(message)
