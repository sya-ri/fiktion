package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.java.JavaFiktionConfig
import dev.s7a.fiktion.generators.int
import java.time.ZoneOffset

/**
 * Generates a Java zone offset from UTC-12 through UTC+14.
 */
public fun FakeContext.zoneOffset(): ZoneOffset = ZoneOffset.ofHours(int(config(JavaFiktionConfig.ZoneOffset.hours)))
