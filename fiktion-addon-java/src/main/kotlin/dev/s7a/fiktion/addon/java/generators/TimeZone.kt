package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.util.TimeZone

/**
 * Generates a Java time zone from generated Java zone ids.
 */
public fun FakeContext.timeZone(): TimeZone = TimeZone.getTimeZone(zoneId())
