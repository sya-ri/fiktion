package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.util.logging.Level

/**
 * Generates a Java logging level.
 */
public fun FakeContext.level(): Level = oneOf(JAVA_LOG_LEVELS)

private val JAVA_LOG_LEVELS: List<Level> =
    listOf(
        Level.OFF,
        Level.SEVERE,
        Level.WARNING,
        Level.INFO,
        Level.CONFIG,
        Level.FINE,
        Level.FINER,
        Level.FINEST,
        Level.ALL,
    )
