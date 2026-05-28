package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.util.UUID

/**
 * Generates a Java UUID.
 */
public fun FakeContext.uuid(): UUID = UUID(long(), long())
