@file:OptIn(ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

private const val DEFAULT_MAX_DURATION_MILLIS = 3_153_600_000_000L

/**
 * Generates a finite duration within approximately plus or minus 100 years.
 */
public fun FakeContext.duration(): Duration =
    long(-DEFAULT_MAX_DURATION_MILLIS..DEFAULT_MAX_DURATION_MILLIS).toDuration(DurationUnit.MILLISECONDS)
