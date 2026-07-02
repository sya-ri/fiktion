@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates an unsigned integer array.
 */
public fun FakeContext.uintArray(size: Int = FiktionConfig.Array.size()): UIntArray = UIntArray(size) { uint() }
