package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an assertion error.
 */
public fun FakeContext.assertionError(message: String = string()): AssertionError = AssertionError(message)
