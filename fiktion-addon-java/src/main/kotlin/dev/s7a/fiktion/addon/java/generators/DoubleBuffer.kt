package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.doubleArray
import java.nio.DoubleBuffer

/**
 * Generates a Java double buffer backed by a random double array.
 */
public fun FakeContext.doubleBuffer(): DoubleBuffer = DoubleBuffer.wrap(doubleArray())
