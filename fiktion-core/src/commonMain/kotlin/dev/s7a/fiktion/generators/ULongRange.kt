package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a finite unsigned long range.
 */
public fun FakeContext.ulongRange(): ULongRange =
    config(FiktionConfig.ULongRange.bounds).let { range ->
        ulongRange(range.first, range.last)
    }

/**
 * Generates a finite unsigned long range from [min] to [max].
 */
public fun FakeContext.ulongRange(
    min: ULong,
    max: ULong,
): ULongRange {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    val first = ulong(min, max)
    val last = ulong(first, max)
    return first..last
}
