package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite long progression.
 */
public fun FakeContext.longProgression(): LongProgression =
    config(FiktionConfig.LongProgression.bounds).let { range ->
        longRange(min = range.first, max = range.last) step long(config(FiktionConfig.LongProgression.step))
    }
