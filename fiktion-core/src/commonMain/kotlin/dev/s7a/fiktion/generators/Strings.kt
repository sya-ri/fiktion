package dev.s7a.fiktion.generators

import dev.s7a.fiktion.requireFiktionConfiguration

/**
 * Default string length range used by [string].
 */
private val DefaultStringLength: IntRange = 8..16

/**
 * Generates a string with a random length from [length] using [charset].
 */
public fun FakeContext.string(
    length: IntRange = DefaultStringLength,
    charset: FiktionCharset = FiktionCharset.AlphaNumeric,
): String {
    requireFiktionConfiguration(!length.isEmpty()) { "length must not be empty." }
    return string(length = length.random(random), charset = charset)
}

/**
 * Generates a string with [length] characters using [charset].
 */
public fun FakeContext.string(
    length: Int,
    charset: FiktionCharset = FiktionCharset.AlphaNumeric,
): String {
    requireFiktionConfiguration(length >= 0) { "length must be greater than or equal to 0." }
    val chars = charset.chars
    val charCount = chars.length
    requireFiktionConfiguration(charCount > 0) { "charset must not be empty." }
    return buildString {
        repeat(length) {
            append(chars[random.nextInt(charCount)])
        }
    }
}
