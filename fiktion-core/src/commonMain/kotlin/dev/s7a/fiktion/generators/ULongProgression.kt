package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite unsigned long progression.
 */
public fun FakeContext.ulongProgression(): ULongProgression =
    config(FiktionConfig.ULongProgression.bounds).let { range ->
        ulongRange(min = range.first, max = range.last) step long(config(FiktionConfig.ULongProgression.step))
    }
