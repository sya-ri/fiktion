package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a byte array.
 */
public fun FakeContext.byteArray(size: Int = FiktionConfig.Array.size()): ByteArray = ByteArray(size) { byte() }
