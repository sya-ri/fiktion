package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.nio.charset.Charset

/**
 * Generates a Java charset from the available JDK charsets.
 */
public fun FakeContext.charset(): Charset = oneOf(JAVA_CHARSETS)

private val JAVA_CHARSETS: List<Charset> = Charset.availableCharsets().values.sortedBy { charset -> charset.name() }
