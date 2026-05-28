@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an unsigned byte array.
 */
public fun FakeContext.ubyteArray(size: Int = int(1..3)): UByteArray = UByteArray(size) { ubyte() }
