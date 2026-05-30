@file:OptIn(ExperimentalTime::class)

package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

/**
 * Generates a finite duration within approximately plus or minus 100 years.
 */
public fun FakeContext.duration(): Duration =
    config(FiktionConfig.Duration.range).let { range ->
        long(range.start.inWholeMilliseconds..range.endInclusive.inWholeMilliseconds).milliseconds
    }
