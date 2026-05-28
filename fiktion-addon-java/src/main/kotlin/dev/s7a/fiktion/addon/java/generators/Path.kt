package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.nio.file.Path

/**
 * Generates a Java NIO path value without touching the filesystem.
 */
public fun FakeContext.path(): Path = file().toPath()
