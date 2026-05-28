package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates an integer across the full integer range.
 */
public fun FakeContext.int(): Int = random.nextInt()

/**
 * Generates an integer from [min] to [max].
 */
public fun FakeContext.int(
    min: Int,
    max: Int,
): Int {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return (min..max).random(random)
}

/**
 * Generates an integer within [range].
 */
public fun FakeContext.int(range: IntRange): Int {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return int(range.first, range.last)
}
