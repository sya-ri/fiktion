package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.java.JavaFiktionConfig
import dev.s7a.fiktion.generators.long
import java.time.Duration

/**
 * Generates a Java duration within approximately plus or minus 100 years.
 */
public fun FakeContext.duration(): Duration = Duration.ofMillis(long(config(JavaFiktionConfig.Duration.millis)))
