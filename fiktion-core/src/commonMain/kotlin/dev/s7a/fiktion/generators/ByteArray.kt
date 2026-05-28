package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a byte array.
 */
public fun FakeContext.byteArray(size: Int = int(1..3)): ByteArray = ByteArray(size) { byte() }
