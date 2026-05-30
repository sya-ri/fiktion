package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a finite character range.
 */
public fun FakeContext.charRange(): CharRange = charRange(oneOf(config(FiktionConfig.Char.rangeCharsets)))

/**
 * Generates a finite character range inside [charset].
 */
public fun FakeContext.charRange(charset: FiktionCharset): CharRange {
    val bounds = charset.contiguousBounds()
    return charRange(min = bounds.first, max = bounds.last)
}

/**
 * Generates a finite character range from [min] to [max].
 */
public fun FakeContext.charRange(
    min: Char,
    max: Char,
): CharRange {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    val first = char(min, max)
    val last = char(first, max)
    return first..last
}

private fun FiktionCharset.contiguousBounds(): CharRange {
    requireFiktionConfiguration(chars.isNotEmpty()) { "charset must not be empty." }
    val sorted = chars.toSet().sorted()
    requireFiktionConfiguration(sorted.zipWithNext().all { (previous, next) -> next.code == previous.code + 1 }) {
        "charset must contain contiguous characters."
    }
    return sorted.first()..sorted.last()
}
