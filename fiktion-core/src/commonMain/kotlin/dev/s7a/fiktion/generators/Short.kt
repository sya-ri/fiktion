package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a short across the full short range.
 */
public fun FakeContext.short(): Short = short(min = Short.MIN_VALUE, max = Short.MAX_VALUE)

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
