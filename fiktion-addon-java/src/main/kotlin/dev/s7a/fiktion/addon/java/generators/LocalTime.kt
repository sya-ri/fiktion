package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.time.LocalTime

/**
 * Generates a Java local time.
 */
public fun FakeContext.localTime(): LocalTime = LocalTime.ofNanoOfDay(long(0 until NANOS_PER_DAY))

private const val NANOS_PER_DAY: Long = 86_400_000_000_000L
