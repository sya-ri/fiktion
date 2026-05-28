package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import java.time.Instant

/**
 * Generates a Java instant from 1900-01-01T00:00:00Z until 2101-01-01T00:00:00Z.
 */
public fun FakeContext.instant(): Instant =
    Instant.ofEpochSecond(
        long(DEFAULT_MIN_EPOCH_SECOND until DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE),
        int(0 until NANOS_PER_SECOND).toLong(),
    )

private val DEFAULT_MIN_EPOCH_SECOND: Long = Instant.parse("1900-01-01T00:00:00Z").epochSecond
private val DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE: Long = Instant.parse("2101-01-01T00:00:00Z").epochSecond
private const val NANOS_PER_SECOND: Int = 1_000_000_000
