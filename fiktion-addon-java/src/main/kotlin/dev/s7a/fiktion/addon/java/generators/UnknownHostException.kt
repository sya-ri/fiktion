package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.net.UnknownHostException

/**
 * Generates a Java unknown host exception.
 */
public fun FakeContext.unknownHostException(message: String = string()): UnknownHostException = UnknownHostException(message)
