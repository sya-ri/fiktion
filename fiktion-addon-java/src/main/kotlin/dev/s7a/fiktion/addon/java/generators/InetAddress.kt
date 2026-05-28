package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import java.net.InetAddress

/**
 * Generates a loopback Java inet address.
 */
public fun FakeContext.inetAddress(): InetAddress = InetAddress.getByAddress(byteArrayOf(127, 0, 0, int(1..MAX_LOOPBACK_HOST).toByte()))

private const val MAX_LOOPBACK_HOST: Int = 254
