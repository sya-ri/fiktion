package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a short array.
 */
public fun FakeContext.shortArray(size: Int = FiktionConfig.Array.size()): ShortArray = ShortArray(size) { short() }
