package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.net.MalformedURLException

/**
 * Generates a Java malformed URL exception.
 */
public fun FakeContext.malformedUrlException(message: String = string()): MalformedURLException = MalformedURLException(message)
