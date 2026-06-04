package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.addon.java.JavaFiktionConfig
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import java.time.Instant

/**
 * Generates a Java instant from 1900-01-01T00:00:00Z until 2101-01-01T00:00:00Z.
 */
public fun FakeContext.instant(): Instant =
    Instant.ofEpochSecond(
        long(config(JavaFiktionConfig.Instant.epochSeconds)),
        int(config(JavaFiktionConfig.Instant.nanosecond)).toLong(),
    )
