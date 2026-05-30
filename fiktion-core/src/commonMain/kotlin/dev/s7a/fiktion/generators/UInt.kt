package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.random.Random
import kotlin.ranges.ClosedRange

/**
 * Generates an unsigned integer across the full unsigned integer range.
 */
public fun FakeContext.uint(): UInt = uint(config(FiktionConfig.UInt.range))

/**
 * Generates an unsigned integer from [min] to [max].
 */
public fun FakeContext.uint(
    min: UInt,
    max: UInt,
): UInt {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return (min..max).random(random)
}

/**
 * Generates an unsigned integer within [range].
 */
public fun FakeContext.uint(range: UIntRange): UInt {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return uint(range.first, range.last)
}

/**
 * Generates an unsigned integer within [range].
 */
public fun FakeContext.uint(range: ClosedRange<UInt>): UInt {
    requireFiktionConfiguration(range.start <= range.endInclusive) { "range must not be empty." }
    return uint(range.start, range.endInclusive)
}

private fun UIntRange.random(random: Random): UInt {
    val size = last - first + 1u
    val value = random.nextInt().toUInt()
    return if (size == 0u) value else first + (value % size)
}
