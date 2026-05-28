package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a long array.
 */
public fun FakeContext.longArray(size: Int = int(1..3)): LongArray = LongArray(size) { long() }
