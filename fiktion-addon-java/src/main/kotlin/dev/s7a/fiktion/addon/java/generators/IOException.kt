package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.IOException

/**
 * Generates a Java IO exception.
 */
public fun FakeContext.ioException(message: String = string()): IOException = IOException(message)
