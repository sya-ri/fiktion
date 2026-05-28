package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.net.URL

/**
 * Generates a Java URL.
 */
public fun FakeContext.url(): URL = uri().toURL()
