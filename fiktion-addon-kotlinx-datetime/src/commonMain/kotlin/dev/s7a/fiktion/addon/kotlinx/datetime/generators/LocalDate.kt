package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.LocalDate

/**
 * Generates a kotlinx-datetime local date.
 */
public fun FakeContext.localDate(): LocalDate =
    LocalDate(
        int(1900, 2100),
        int(1, 12),
        int(1, 28),
    )
