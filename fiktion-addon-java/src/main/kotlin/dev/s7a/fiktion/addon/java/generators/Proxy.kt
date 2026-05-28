package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.oneOf
import java.net.Proxy

/**
 * Generates a Java proxy.
 */
public fun FakeContext.proxy(): Proxy =
    if (boolean()) {
        Proxy.NO_PROXY
    } else {
        Proxy(oneOf(PROXY_ADDRESS_TYPES), inetSocketAddress())
    }

private val PROXY_ADDRESS_TYPES: List<Proxy.Type> = listOf(Proxy.Type.HTTP, Proxy.Type.SOCKS)
