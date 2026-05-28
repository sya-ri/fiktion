package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.io.File

/**
 * Generates a Java file path value without touching the filesystem.
 */
public fun FakeContext.file(): File = File(fileName())
