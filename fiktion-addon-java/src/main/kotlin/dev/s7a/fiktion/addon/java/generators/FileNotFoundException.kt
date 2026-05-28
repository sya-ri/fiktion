package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.string
import java.io.FileNotFoundException

/**
 * Generates a Java file-not-found exception.
 */
public fun FakeContext.fileNotFoundException(message: String = string()): FileNotFoundException = FileNotFoundException(message)
