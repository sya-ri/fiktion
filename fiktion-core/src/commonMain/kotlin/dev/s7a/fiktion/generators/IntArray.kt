package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an integer array.
 */
public fun FakeContext.intArray(size: Int = int(1..3)): IntArray = IntArray(size) { int() }
