package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.byteArray
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Generates a Java input stream backed by random bytes.
 */
public fun FakeContext.inputStream(): InputStream = ByteArrayInputStream(byteArray())
