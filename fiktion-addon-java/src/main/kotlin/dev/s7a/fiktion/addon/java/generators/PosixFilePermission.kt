package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.nio.file.attribute.PosixFilePermission

/**
 * Generates a Java POSIX file permission.
 */
public fun FakeContext.posixFilePermission(): PosixFilePermission = oneOf(PosixFilePermission.entries)
