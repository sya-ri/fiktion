package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.ZonedDateTime

/**
 * Generates a Java zoned date-time.
 */
public fun FakeContext.zonedDateTime(): ZonedDateTime = ZonedDateTime.of(localDateTime(), zoneId())
