package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.net.SocketAddress

/**
 * Generates a Java socket address.
 */
public fun FakeContext.socketAddress(): SocketAddress = inetSocketAddress()
