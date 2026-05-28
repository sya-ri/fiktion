package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.charArray
import java.nio.CharBuffer

/**
 * Generates a Java char buffer backed by a random char array.
 */
public fun FakeContext.charBuffer(): CharBuffer = CharBuffer.wrap(charArray())
