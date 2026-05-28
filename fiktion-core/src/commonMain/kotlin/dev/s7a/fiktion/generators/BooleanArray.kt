package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a boolean array.
 */
public fun FakeContext.booleanArray(size: Int = int(1..3)): BooleanArray = BooleanArray(size) { boolean() }
