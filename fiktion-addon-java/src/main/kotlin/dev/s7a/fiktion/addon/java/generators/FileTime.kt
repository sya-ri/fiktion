package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.nio.file.attribute.FileTime

/**
 * Generates a Java file time.
 */
public fun FakeContext.fileTime(): FileTime = FileTime.from(instant())
