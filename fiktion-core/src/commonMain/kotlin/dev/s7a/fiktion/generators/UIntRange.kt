package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a finite unsigned integer range.
 */
public fun FakeContext.uintRange(): UIntRange = uintRange(min = UInt.MIN_VALUE, max = UInt.MAX_VALUE)

/**
 * Generates a finite unsigned integer range from [min] to [max].
 */
public fun FakeContext.uintRange(
    min: UInt,
    max: UInt,
): UIntRange {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    val first = uint(min, max)
    val last = uint(first, max)
    return first..last
}
