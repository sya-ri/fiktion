package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.IOException
import java.io.UncheckedIOException

/**
 * Generates a Java unchecked IO exception.
 */
public fun FakeContext.uncheckedIoException(message: String = string()): UncheckedIOException =
    UncheckedIOException(message, IOException(message))
