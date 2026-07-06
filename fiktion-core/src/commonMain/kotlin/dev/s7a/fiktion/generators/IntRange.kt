package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a finite integer range.
 */
public fun FakeContext.intRange(): IntRange = FiktionConfig.IntRange.bounds()

/**
 * Generates a finite integer range from [min] to [max].
 */
public fun FakeContext.intRange(
    min: Int,
    max: Int,
): IntRange {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    val first = int(min, max)
    val last = int(first, max)
    return first..last
}
