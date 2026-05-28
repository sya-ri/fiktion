package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.net.SocketException

/**
 * Generates a Java socket exception.
 */
public fun FakeContext.socketException(message: String = string()): SocketException = SocketException(message)
