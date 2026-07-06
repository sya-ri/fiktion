package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a regex whose pattern is a literal alpha-numeric string.
 */
public fun FakeContext.regex(
    length: Int = FiktionConfig.Regex.length(),
    charset: FiktionCharset = FiktionConfig.Regex.charset.get(),
): Regex = Regex(string(length = length, charset = charset))
