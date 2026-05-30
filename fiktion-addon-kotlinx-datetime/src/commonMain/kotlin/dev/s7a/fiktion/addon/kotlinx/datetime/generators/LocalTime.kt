package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import kotlinx.datetime.LocalTime

/**
 * Generates a kotlinx-datetime local time.
 */
public fun FakeContext.localTime(): LocalTime =
    LocalTime(
        hour = int(0, 23),
        minute = int(0, 59),
        second = int(0, 59),
        nanosecond = int(0, 999_999_999),
    )
