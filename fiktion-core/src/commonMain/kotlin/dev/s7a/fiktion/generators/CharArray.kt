package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a character array.
 */
public fun FakeContext.charArray(size: Int = int(1..3)): CharArray = CharArray(size) { char() }
