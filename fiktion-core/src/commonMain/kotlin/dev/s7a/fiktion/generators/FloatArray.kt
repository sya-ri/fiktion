package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a float array.
 */
public fun FakeContext.floatArray(size: Int = int(1..3)): FloatArray = FloatArray(size) { float() }
