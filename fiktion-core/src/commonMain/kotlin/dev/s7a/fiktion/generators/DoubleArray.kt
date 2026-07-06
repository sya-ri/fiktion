package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a double array.
 */
public fun FakeContext.doubleArray(size: Int = FiktionConfig.Array.size()): DoubleArray = DoubleArray(size) { double() }
