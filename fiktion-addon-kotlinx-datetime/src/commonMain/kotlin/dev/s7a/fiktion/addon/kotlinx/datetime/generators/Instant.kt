@file:Suppress("DEPRECATION")

package dev.s7a.fiktion.addon.kotlinx.datetime.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.long
import kotlinx.datetime.Instant

/**
 * Generates a kotlinx-datetime instant from 1900-01-01T00:00:00Z until 2101-01-01T00:00:00Z.
 */
public fun FakeContext.instant(): Instant =
    Instant.fromEpochSeconds(
        long(DEFAULT_MIN_EPOCH_SECOND until DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE),
        int(0 until NANOS_PER_SECOND),
    )

private const val DEFAULT_MIN_EPOCH_SECOND: Long = -2_208_988_800L
private const val DEFAULT_MAX_EPOCH_SECOND_EXCLUSIVE: Long = 4_134_844_800L
private const val NANOS_PER_SECOND: Int = 1_000_000_000
