package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import kotlinx.datetime.TimeZone

/**
 * Generates a kotlinx-datetime time zone.
 */
public fun FakeContext.timeZone(): TimeZone = TimeZone.UTC
