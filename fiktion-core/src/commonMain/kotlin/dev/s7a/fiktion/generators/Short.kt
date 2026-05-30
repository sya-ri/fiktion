package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.ranges.ClosedRange

/**
 * Generates a short across the full short range.
 */
public fun FakeContext.short(): Short = short(config(FiktionConfig.Short.range))

/**
 * Generates a short from [min] to [max].
 */
public fun FakeContext.short(
    min: Short,
    max: Short,
): Short {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return int(min.toInt(), max.toInt()).toShort()
}

/**
 * Generates a short within [range].
 */
public fun FakeContext.short(range: IntRange): Short {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    requireFiktionConfiguration(range.first >= Short.MIN_VALUE && range.last <= Short.MAX_VALUE) {
        "range must be inside Short bounds."
    }
    return short(range.first.toShort(), range.last.toShort())
}

/**
 * Generates a short within [range].
 */
public fun FakeContext.short(range: ClosedRange<Int>): Short {
    requireFiktionConfiguration(range.start <= range.endInclusive) { "range must not be empty." }
    requireFiktionConfiguration(range.start >= Short.MIN_VALUE && range.endInclusive <= Short.MAX_VALUE) {
        "range must be inside Short bounds."
    }
    return short(range.start.toShort(), range.endInclusive.toShort())
}
