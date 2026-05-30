package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import kotlinx.datetime.DateTimePeriod

/**
 * Generates a kotlinx-datetime date-time period.
 */
public fun FakeContext.dateTimePeriod(): DateTimePeriod =
    DateTimePeriod(
        years = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.years)),
        months = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.months)),
        days = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.days)),
        hours = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.hours)),
        minutes = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.minutes)),
        seconds = int(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.seconds)),
        nanoseconds = long(config(KotlinxDatetimeFiktionConfig.DateTimePeriod.nanoseconds)),
    )
