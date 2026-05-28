package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a short array.
 */
public fun FakeContext.shortArray(size: Int = int(1..3)): ShortArray = ShortArray(size) { short() }
