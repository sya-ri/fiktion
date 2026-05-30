package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a float array.
 */
public fun FakeContext.floatArray(size: Int = int(config(FiktionConfig.Array.size))): FloatArray = FloatArray(size) { float() }
