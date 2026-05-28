package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import java.time.OffsetTime

/**
 * Generates a Java offset time.
 */
public fun FakeContext.offsetTime(): OffsetTime = OffsetTime.of(localTime(), zoneOffset())
