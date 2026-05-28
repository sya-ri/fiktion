package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Default string length range used by [string].
 */
private val DEFAULT_STRING_LENGTH: IntRange = 1..32

/**
 * Generates a string with a random length from [length] using [charset].
 */
public fun FakeContext.string(
    length: IntRange = DEFAULT_STRING_LENGTH,
    charset: FiktionCharset = FiktionCharset.AlphaNumeric,
): String {
    require(!length.isEmpty()) { "length must not be empty." }
    return string(length = int(length), charset = charset)
}

/**
 * Generates a string with [length] characters using [charset].
 */
public fun FakeContext.string(
    length: Int,
    charset: FiktionCharset = FiktionCharset.AlphaNumeric,
): String {
    require(length >= 0) { "length must be greater than or equal to 0." }
    require(charset.chars.isNotEmpty()) { "charset must not be empty." }
    return buildString {
        repeat(length) {
            append(oneOf(charset.chars.toList()))
        }
    }
}
