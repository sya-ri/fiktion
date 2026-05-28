package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import java.time.ZoneOffset

/**
 * Generates a Java zone offset from UTC-12 through UTC+14.
 */
public fun FakeContext.zoneOffset(): ZoneOffset = ZoneOffset.ofHours(int(MIN_ZONE_OFFSET_HOURS..MAX_ZONE_OFFSET_HOURS))

private const val MIN_ZONE_OFFSET_HOURS: Int = -12
private const val MAX_ZONE_OFFSET_HOURS: Int = 14
