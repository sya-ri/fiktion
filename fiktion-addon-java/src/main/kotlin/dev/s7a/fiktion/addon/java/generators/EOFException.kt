package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.EOFException

/**
 * Generates a Java EOF exception.
 */
public fun FakeContext.eofException(message: String = string()): EOFException = EOFException(message)
