package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.util.zip.ZipEntry

/**
 * Generates a Java zip entry.
 */
public fun FakeContext.zipEntry(): ZipEntry = ZipEntry(fileName())
