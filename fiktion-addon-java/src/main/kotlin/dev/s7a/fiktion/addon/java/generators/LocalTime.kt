package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.java.JavaFiktionConfig
import dev.s7a.fiktion.generators.long
import java.time.LocalTime

/**
 * Generates a Java local time.
 */
public fun FakeContext.localTime(): LocalTime = LocalTime.ofNanoOfDay(long(config(JavaFiktionConfig.LocalTime.nanosecondsOfDay)))
