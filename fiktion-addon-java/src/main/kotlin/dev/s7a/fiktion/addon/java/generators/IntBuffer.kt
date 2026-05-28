package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.intArray
import java.nio.IntBuffer

/**
 * Generates a Java int buffer backed by a random int array.
 */
public fun FakeContext.intBuffer(): IntBuffer = IntBuffer.wrap(intArray())
