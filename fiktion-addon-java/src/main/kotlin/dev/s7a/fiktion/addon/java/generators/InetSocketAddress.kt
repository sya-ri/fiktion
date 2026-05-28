package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import java.net.InetSocketAddress

/**
 * Generates a Java inet socket address on the loopback address.
 */
public fun FakeContext.inetSocketAddress(): InetSocketAddress = InetSocketAddress(inetAddress(), int(MIN_PORT..MAX_PORT))

private const val MIN_PORT: Int = 1_024
private const val MAX_PORT: Int = 65_535
