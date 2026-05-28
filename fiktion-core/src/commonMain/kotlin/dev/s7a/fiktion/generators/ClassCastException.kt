package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a class cast exception.
 */
public fun FakeContext.classCastException(message: String = string()): ClassCastException = ClassCastException(message)
