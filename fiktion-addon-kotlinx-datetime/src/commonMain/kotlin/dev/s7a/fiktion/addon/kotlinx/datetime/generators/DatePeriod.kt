package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.DatePeriod

/**
 * Generates a kotlinx-datetime date period.
 */
public fun FakeContext.datePeriod(): DatePeriod =
    DatePeriod(
        years = int(config(KotlinxDatetimeFiktionConfig.DatePeriod.years)),
        months = int(config(KotlinxDatetimeFiktionConfig.DatePeriod.months)),
        days = int(config(KotlinxDatetimeFiktionConfig.DatePeriod.days)),
    )
