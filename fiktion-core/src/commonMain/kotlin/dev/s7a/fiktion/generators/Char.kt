package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Generates a character from the default alpha-numeric character set.
 */
public fun FakeContext.char(charset: FiktionCharset = FiktionConfig.Char.charset.get()): Char =
    string(length = 1, charset = charset).single()

/**
 * Generates a character from [min] to [max].
 */
public fun FakeContext.char(
    min: Char,
    max: Char,
): Char {
    requireFiktionConfiguration(min <= max) { "min must be less than or equal to max." }
    return int(min.code, max.code).toChar()
}

/**
 * Generates a character within [range].
 */
public fun FakeContext.char(range: CharRange): Char {
    requireFiktionConfiguration(!range.isEmpty()) { "range must not be empty." }
    return char(range.first, range.last)
}
