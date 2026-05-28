package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an error.
 */
public fun FakeContext.error(message: String = string()): Error = Error(message)
