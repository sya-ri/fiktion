package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.random.Random

/**
 * Generates a double in `0.0 <= value < 1.0`.
 */
public fun FakeContext.double(): Double = double(config(FiktionConfig.Double.range))

/**
 * Generates a double from [min] to [max].
 */
public fun FakeContext.double(
    min: Double,
    max: Double,
): Double {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    requireFiktionConfiguration(min.isFinite() && max.isFinite()) { "range bounds must be finite." }
    return random.nextDouble(min..max)
}

/**
 * Generates a double within [range].
 */
public fun FakeContext.double(range: ClosedFloatingPointRange<Double>): Double {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return double(range.start, range.endInclusive)
}

private fun Random.nextDouble(range: ClosedFloatingPointRange<Double>): Double =
    range.start + nextDouble() * (range.endInclusive - range.start)
