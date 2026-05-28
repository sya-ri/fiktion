package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.byteArray
import java.nio.ByteBuffer

/**
 * Generates a Java byte buffer backed by a random byte array.
 */
public fun FakeContext.byteBuffer(): ByteBuffer = ByteBuffer.wrap(byteArray())
