package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.oneOf
import kotlinx.datetime.TimeZone

/**
 * Generates a kotlinx-datetime time zone.
 */
public fun FakeContext.timeZone(): TimeZone = TimeZone.of(oneOf(TimeZone.availableZoneIds.toList()))
