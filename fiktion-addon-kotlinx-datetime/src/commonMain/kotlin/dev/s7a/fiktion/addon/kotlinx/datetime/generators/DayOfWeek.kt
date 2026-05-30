package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import kotlinx.datetime.DayOfWeek

/**
 * Generates a kotlinx-datetime day of week.
 */
public fun FakeContext.dayOfWeek(): DayOfWeek = oneOf(DayOfWeek.entries)
