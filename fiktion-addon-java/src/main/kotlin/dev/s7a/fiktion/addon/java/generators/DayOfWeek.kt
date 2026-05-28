package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import java.time.DayOfWeek

/**
 * Generates a Java day of week.
 */
public fun FakeContext.dayOfWeek(): DayOfWeek = oneOf(DayOfWeek.entries)
