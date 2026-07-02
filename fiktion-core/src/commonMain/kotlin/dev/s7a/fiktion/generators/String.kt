package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import kotlin.ranges.ClosedRange

/**
 * Generates a string with a random length from [length] using [charset].
 */
public fun FakeContext.string(
    length: ClosedRange<Int>,
    charset: FiktionCharset = FiktionConfig.String.charset.get(),
): String = string(length = int(length), charset = charset)

/**
 * Generates a string with [length] characters using [charset].
 */
public fun FakeContext.string(
    length: Int = FiktionConfig.String.length(),
    charset: FiktionCharset = FiktionConfig.String.charset.get(),
): String {
    require(length >= 0) { "length must be greater than or equal to 0." }
    require(charset.chars.isNotEmpty()) { "charset must not be empty." }
    return buildString {
        repeat(length) {
            append(oneOf(charset.chars.toList()))
        }
    }
}
