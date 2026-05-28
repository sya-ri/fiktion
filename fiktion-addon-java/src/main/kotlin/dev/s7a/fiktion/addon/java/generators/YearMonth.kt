package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.YearMonth

/**
 * Generates a Java year-month.
 */
public fun FakeContext.yearMonth(): YearMonth = YearMonth.from(localDate())
