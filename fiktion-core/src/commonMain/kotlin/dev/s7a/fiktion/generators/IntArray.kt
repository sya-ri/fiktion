package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates an integer array.
 */
public fun FakeContext.intArray(size: Int = int(config(FiktionConfig.Array.size))): IntArray = IntArray(size) { int() }
