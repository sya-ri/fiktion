package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.time.Duration

/**
 * Generates a Java duration within approximately plus or minus 100 years.
 */
public fun FakeContext.duration(): Duration = Duration.ofMillis(long(-DEFAULT_MAX_DURATION_MILLIS..DEFAULT_MAX_DURATION_MILLIS))

private const val DEFAULT_MAX_DURATION_MILLIS: Long = 3_153_600_000_000L
