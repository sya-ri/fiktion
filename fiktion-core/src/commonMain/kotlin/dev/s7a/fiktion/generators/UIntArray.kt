@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an unsigned integer array.
 */
public fun FakeContext.uintArray(size: Int = int(1..3)): UIntArray = UIntArray(size) { uint() }
