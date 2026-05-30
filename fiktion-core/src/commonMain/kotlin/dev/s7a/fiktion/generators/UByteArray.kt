@file:OptIn(ExperimentalUnsignedTypes::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates an unsigned byte array.
 */
public fun FakeContext.ubyteArray(size: Int = int(config(FiktionConfig.Array.size))): UByteArray = UByteArray(size) { ubyte() }
