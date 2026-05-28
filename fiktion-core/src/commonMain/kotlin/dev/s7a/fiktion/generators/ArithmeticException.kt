package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an arithmetic exception.
 */
public fun FakeContext.arithmeticException(message: String = string()): ArithmeticException = ArithmeticException(message)
