package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.StringWriter
import java.io.Writer

/**
 * Generates a Java writer containing a random string.
 */
public fun FakeContext.writer(): Writer =
    StringWriter().apply {
        write(string())
    }
