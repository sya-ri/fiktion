package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.Reader
import java.io.StringReader

/**
 * Generates a Java reader backed by a random string.
 */
public fun FakeContext.reader(): Reader = StringReader(string())
