package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an exception.
 */
public fun FakeContext.exception(message: String = string()): Exception = Exception(message)
