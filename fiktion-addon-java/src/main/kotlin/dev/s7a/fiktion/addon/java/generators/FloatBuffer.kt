package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.floatArray
import java.nio.FloatBuffer

/**
 * Generates a Java float buffer backed by a random float array.
 */
public fun FakeContext.floatBuffer(): FloatBuffer = FloatBuffer.wrap(floatArray())
