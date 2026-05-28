package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an index out of bounds exception.
 */
public fun FakeContext.indexOutOfBoundsException(message: String = string()): IndexOutOfBoundsException = IndexOutOfBoundsException(message)
