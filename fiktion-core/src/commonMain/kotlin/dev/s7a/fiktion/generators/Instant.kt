@file:OptIn(kotlin.time.ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import kotlin.time.Instant

private const val DEFAULT_MIN_EPOCH_SECOND = 946_684_800L
private const val DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE = 4_102_444_800L
private const val NANOS_PER_SECOND = 1_000_000_000

/**
 * Generates an instant from 2000-01-01T00:00:00Z until 2100-01-01T00:00:00Z.
 */
public fun FakeContext.instant(): Instant =
    Instant.fromEpochSeconds(
        epochSeconds = long(DEFAULT_MIN_EPOCH_SECOND..<DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE),
        nanosecondAdjustment = int(0..<NANOS_PER_SECOND),
    )
