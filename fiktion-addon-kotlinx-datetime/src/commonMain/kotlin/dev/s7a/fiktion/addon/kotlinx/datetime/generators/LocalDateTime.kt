package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import kotlinx.datetime.LocalDateTime

/**
 * Generates a kotlinx-datetime local date-time.
 */
public fun FakeContext.localDateTime(): LocalDateTime = LocalDateTime(date = localDate(), time = localTime())
