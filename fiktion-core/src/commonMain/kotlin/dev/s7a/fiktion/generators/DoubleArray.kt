package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a double array.
 */
public fun FakeContext.doubleArray(size: Int = int(1..3)): DoubleArray = DoubleArray(size) { double() }
