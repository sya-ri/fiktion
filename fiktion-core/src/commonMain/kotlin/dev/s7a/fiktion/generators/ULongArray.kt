@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates an unsigned long array.
 */
public fun FakeContext.ulongArray(size: Int = int(config(FiktionConfig.Array.size))): ULongArray = ULongArray(size) { ulong() }
