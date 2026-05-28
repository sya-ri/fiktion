package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a number format exception.
 */
public fun FakeContext.numberFormatException(message: String = string()): NumberFormatException = NumberFormatException(message)
