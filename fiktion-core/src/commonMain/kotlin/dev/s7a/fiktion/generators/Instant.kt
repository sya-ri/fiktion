@file:OptIn(ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Generates an instant from 2000-01-01T00:00:00Z until 2100-01-01T00:00:00Z.
 */
public fun FakeContext.instant(): Instant =
    Instant.fromEpochSeconds(
        epochSeconds = FiktionConfig.Instant.epochSeconds(),
        nanosecondAdjustment = FiktionConfig.Instant.nanosecond(),
    )
