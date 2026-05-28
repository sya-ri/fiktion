@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates an unsigned long array.
 */
public fun FakeContext.ulongArray(size: Int = int(1..3)): ULongArray = ULongArray(size) { ulong() }
