package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.MonthDay

/**
 * Generates a Java month-day.
 */
public fun FakeContext.monthDay(): MonthDay = MonthDay.from(localDate())
