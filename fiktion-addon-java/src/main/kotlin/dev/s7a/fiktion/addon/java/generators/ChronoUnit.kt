package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.time.temporal.ChronoUnit

/**
 * Generates a Java chrono unit.
 */
public fun FakeContext.chronoUnit(): ChronoUnit = oneOf(ChronoUnit.entries)
