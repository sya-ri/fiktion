package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a regex whose pattern is a literal alpha-numeric string.
 */
public fun FakeContext.regex(
    length: Int = 8,
    charset: FiktionCharset = FiktionCharset.AlphaNumeric,
): Regex = Regex(string(length = length, charset = charset))
