package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates an unsigned short across the full unsigned short range.
 */
public fun FakeContext.ushort(): UShort = ushort(config(FiktionConfig.UShort.range))

/**
 * Generates an unsigned short from [min] to [max].
 */
public fun FakeContext.ushort(
    min: UShort,
    max: UShort,
): UShort {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return uint(min.toUInt(), max.toUInt()).toUShort()
}

/**
 * Generates an unsigned short within [range].
 */
public fun FakeContext.ushort(range: UIntRange): UShort {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    requireFiktionConfiguration(range.first >= UShort.MIN_VALUE.toUInt() && range.last <= UShort.MAX_VALUE.toUInt()) {
        "range must be inside UShort bounds."
    }
    return ushort(range.first.toUShort(), range.last.toUShort())
}
