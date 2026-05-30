package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig

/**
 * Generates a regex whose pattern is a literal alpha-numeric string.
 */
public fun FakeContext.regex(
    length: Int = int(config(FiktionConfig.Regex.length)),
    charset: FiktionCharset = config(FiktionConfig.Regex.charset),
): Regex = Regex(string(length = length, charset = charset))
