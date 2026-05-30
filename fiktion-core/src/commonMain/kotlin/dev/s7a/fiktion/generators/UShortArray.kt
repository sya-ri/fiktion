@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates an unsigned short array.
 */
public fun FakeContext.ushortArray(size: Int = int(config(FiktionConfig.Array.size))): UShortArray = UShortArray(size) { ushort() }
