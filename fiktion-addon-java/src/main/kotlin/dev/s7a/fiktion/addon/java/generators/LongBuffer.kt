package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.longArray
import java.nio.LongBuffer

/**
 * Generates a Java long buffer backed by a random long array.
 */
public fun FakeContext.longBuffer(): LongBuffer = LongBuffer.wrap(longArray())
