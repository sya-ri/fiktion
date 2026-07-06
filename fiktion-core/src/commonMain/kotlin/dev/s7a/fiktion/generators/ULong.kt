package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.random.Random
import kotlin.ranges.ClosedRange

/**
 * Generates an unsigned long across the full unsigned long range.
 */
public fun FakeContext.ulong(): ULong = FiktionConfig.ULong.range()

/**
 * Generates an unsigned long from [min] to [max].
 */
public fun FakeContext.ulong(
    min: ULong,
    max: ULong,
): ULong {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return (min..max).random(random)
}

/**
 * Generates an unsigned long within [range].
 */
public fun FakeContext.ulong(range: ULongRange): ULong {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return ulong(range.first, range.last)
}

/**
 * Generates an unsigned long within [range].
 */
public fun FakeContext.ulong(range: ClosedRange<ULong>): ULong {
    requireFiktionConfiguration(range.start <= range.endInclusive) { "range must not be empty." }
    return ulong(range.start, range.endInclusive)
}

private fun ULongRange.random(random: Random): ULong {
    val size = last - first + 1uL
    val value = random.nextLong().toULong()
    return if (size == 0uL) value else first + (value % size)
}
