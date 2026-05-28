package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates an unsigned byte across the full unsigned byte range.
 */
public fun FakeContext.ubyte(): UByte = ubyte(min = UByte.MIN_VALUE, max = UByte.MAX_VALUE)

/**
 * Generates an unsigned byte from [min] to [max].
 */
public fun FakeContext.ubyte(
    min: UByte,
    max: UByte,
): UByte {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return uint(min.toUInt(), max.toUInt()).toUByte()
}

/**
 * Generates an unsigned byte within [range].
 */
public fun FakeContext.ubyte(range: UIntRange): UByte {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    requireFiktionConfiguration(range.first >= UByte.MIN_VALUE.toUInt() && range.last <= UByte.MAX_VALUE.toUInt()) {
        "range must be inside UByte bounds."
    }
    return ubyte(range.first.toUByte(), range.last.toUByte())
}
