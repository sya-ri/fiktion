package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a finite long range.
 */
public fun FakeContext.longRange(): LongRange =
    config(FiktionConfig.LongRange.bounds).let { range ->
        longRange(range.start, range.endInclusive)
    }

/**
 * Generates a finite long range from [min] to [max].
 */
public fun FakeContext.longRange(
    min: Long,
    max: Long,
): LongRange {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    val first = long(min, max)
    val last = long(first, max)
    return first..last
}
