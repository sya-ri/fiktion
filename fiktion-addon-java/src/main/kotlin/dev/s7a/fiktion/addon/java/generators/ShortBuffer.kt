package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.shortArray
import java.nio.ShortBuffer

/**
 * Generates a Java short buffer backed by a random short array.
 */
public fun FakeContext.shortBuffer(): ShortBuffer = ShortBuffer.wrap(shortArray())
