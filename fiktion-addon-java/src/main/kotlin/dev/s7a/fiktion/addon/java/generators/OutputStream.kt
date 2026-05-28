package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.byteArray
import java.io.ByteArrayOutputStream
import java.io.OutputStream

/**
 * Generates a Java output stream containing random bytes.
 */
public fun FakeContext.outputStream(): OutputStream =
    ByteArrayOutputStream().apply {
        write(byteArray())
    }
