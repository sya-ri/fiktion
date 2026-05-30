package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a string with a random length from [length] using [charset].
 */
public fun FakeContext.string(
    length: IntRange = config(FiktionConfig.String.length),
    charset: FiktionCharset = config(FiktionConfig.String.charset),
): String {
    require(!length.isEmpty()) { "length must not be empty." }
    return string(length = int(length), charset = charset)
}

/**
 * Generates a string with [length] characters using [charset].
 */
public fun FakeContext.string(
    length: Int,
    charset: FiktionCharset = config(FiktionConfig.String.charset),
): String {
    require(length >= 0) { "length must be greater than or equal to 0." }
    require(charset.chars.isNotEmpty()) { "charset must not be empty." }
    return buildString {
        repeat(length) {
            append(oneOf(charset.chars.toList()))
        }
    }
}
