package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.kotlinx.datetime.KotlinxDatetimeFiktionConfig
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

/**
 * Generates a kotlinx-datetime local date.
 */
public fun FakeContext.localDate(): LocalDate =
    LocalDate(
        year = int(config(KotlinxDatetimeFiktionConfig.LocalDate.year)),
        month = Month(int(config(KotlinxDatetimeFiktionConfig.LocalDate.month))),
        day = int(config(KotlinxDatetimeFiktionConfig.LocalDate.day)),
    )
