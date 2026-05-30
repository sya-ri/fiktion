package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite integer progression.
 */
public fun FakeContext.intProgression(): IntProgression =
    config(FiktionConfig.IntProgression.bounds).let { range ->
        intRange(min = range.start, max = range.endInclusive) step int(config(FiktionConfig.IntProgression.step))
    }
