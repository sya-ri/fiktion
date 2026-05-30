package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a character array.
 */
public fun FakeContext.charArray(size: Int = int(config(FiktionConfig.Array.size))): CharArray = CharArray(size) { char() }
