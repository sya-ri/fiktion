package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.net.Proxy

/**
 * Generates a Java proxy type.
 */
public fun FakeContext.proxyType(): Proxy.Type = oneOf(Proxy.Type.entries)
