package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a long array.
 */
public fun FakeContext.longArray(size: Int = FiktionConfig.Array.size()): LongArray = LongArray(size) { long() }
