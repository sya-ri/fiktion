package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a boolean array.
 */
public fun FakeContext.booleanArray(size: Int = FiktionConfig.Array.size()): BooleanArray = BooleanArray(size) { boolean() }
