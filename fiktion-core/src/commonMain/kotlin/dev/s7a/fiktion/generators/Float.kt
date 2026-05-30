package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration
import kotlin.random.Random

/**
 * Generates a float in `0.0 <= value < 1.0`.
 */
public fun FakeContext.float(): Float = float(config(FiktionConfig.Float.range))

/**
 * Generates a float from [min] to [max].
 */
public fun FakeContext.float(
    min: Float,
    max: Float,
): Float {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    requireFiktionConfiguration(min.isFinite() && max.isFinite()) { "range bounds must be finite." }
    return random.nextFloat(min..max)
}

/**
 * Generates a float within [range].
 */
public fun FakeContext.float(range: ClosedFloatingPointRange<Float>): Float {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return float(range.start, range.endInclusive)
}

private fun Random.nextFloat(range: ClosedFloatingPointRange<Float>): Float = range.start + nextFloat() * (range.endInclusive - range.start)
