package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.DatePeriod

/**
 * Generates a kotlinx-datetime date period.
 */
public fun FakeContext.datePeriod(): DatePeriod =
    DatePeriod(
        years = int(-200, 200),
        months = int(-24, 24),
        days = int(-366, 366),
    )
