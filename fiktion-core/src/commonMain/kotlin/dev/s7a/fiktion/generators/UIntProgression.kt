package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a finite unsigned integer progression.
 */
public fun FakeContext.uintProgression(): UIntProgression =
    config(FiktionConfig.UIntProgression.bounds).let { range ->
        uintRange(min = range.start, max = range.endInclusive) step int(config(FiktionConfig.UIntProgression.step))
    }
