package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.ranges.ClosedRange

/**
 * Generates a byte across the full byte range.
 */
public fun FakeContext.byte(): Byte =
    FiktionConfig.Byte
        .range()
        .toByte()

/**
 * Generates a byte from [min] to [max].
 */
public fun FakeContext.byte(
    min: Byte,
    max: Byte,
): Byte {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return int(min.toInt(), max.toInt()).toByte()
}

/**
 * Generates a byte within [range].
 */
public fun FakeContext.byte(range: IntRange): Byte {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    requireFiktionConfiguration(range.first >= Byte.MIN_VALUE && range.last <= Byte.MAX_VALUE) {
        "range must be inside Byte bounds."
    }
    return byte(range.first.toByte(), range.last.toByte())
}

/**
 * Generates a byte within [range].
 */
public fun FakeContext.byte(range: ClosedRange<Int>): Byte {
    requireFiktionConfiguration(range.start <= range.endInclusive) { "range must not be empty." }
    requireFiktionConfiguration(range.start >= Byte.MIN_VALUE && range.endInclusive <= Byte.MAX_VALUE) {
        "range must be inside Byte bounds."
    }
    return byte(range.start.toByte(), range.endInclusive.toByte())
}
