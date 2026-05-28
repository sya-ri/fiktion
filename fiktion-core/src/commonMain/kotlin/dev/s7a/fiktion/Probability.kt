package dev.s7a.fiktion

import kotlin.jvm.JvmInline

/**
 * Probability value represented as a number from `0.0` to `1.0`.
 */
@JvmInline
public value class Probability public constructor(
    /**
     * Raw probability value.
     */
    public val value: Double,
) {
    init {
        requireFiktionConfiguration(value in 0.0..1.0) {
            "Probability must be between 0.0 and 1.0, but was $value."
        }
    }
}

/**
 * Converts this integer percentage to a [Probability].
 */
public val Int.percent: Probability
    get() = Probability(toDouble() / 100.0)

/**
 * Converts this floating-point percentage to a [Probability].
 */
public val Double.percent: Probability
    get() = Probability(this / 100.0)
