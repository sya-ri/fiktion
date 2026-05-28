package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.string
import java.util.regex.Pattern

/**
 * Generates a Java regex pattern that matches one generated literal string.
 */
public fun FakeContext.pattern(): Pattern =
    Pattern.compile(Pattern.quote(string(length = DEFAULT_PATTERN_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric)))

private const val DEFAULT_PATTERN_LENGTH: Int = 8
