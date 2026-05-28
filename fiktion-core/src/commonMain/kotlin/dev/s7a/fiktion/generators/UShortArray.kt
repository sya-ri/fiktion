@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an unsigned short array.
 */
public fun FakeContext.ushortArray(size: Int = int(1..3)): UShortArray = UShortArray(size) { ushort() }
