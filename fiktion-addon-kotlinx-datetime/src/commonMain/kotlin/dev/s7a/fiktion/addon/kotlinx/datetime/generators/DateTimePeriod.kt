package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.DateTimePeriod

/**
 * Generates a kotlinx-datetime date-time period.
 */
public fun FakeContext.dateTimePeriod(): DateTimePeriod =
    DateTimePeriod(
        int(-200, 200),
        int(-24, 24),
        int(-366, 366),
        int(-48, 48),
        int(-120, 120),
        int(-120, 120),
        int(-999_999_999, 999_999_999).toLong(),
    )
