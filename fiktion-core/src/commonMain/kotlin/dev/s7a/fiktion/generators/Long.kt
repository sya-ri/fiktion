package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a long across the full long range.
 */
public fun FakeContext.long(): Long = long(config(FiktionConfig.Long.range))

/**
 * Generates a long from [min] to [max].
 */
public fun FakeContext.long(
    min: Long,
    max: Long,
): Long {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return (min..max).random(random)
}

/**
 * Generates a long within [range].
 */
public fun FakeContext.long(range: LongRange): Long {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return long(range.first, range.last)
}
