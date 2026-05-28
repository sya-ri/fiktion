package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a no such element exception.
 */
public fun FakeContext.noSuchElementException(message: String = string()): NoSuchElementException = NoSuchElementException(message)
