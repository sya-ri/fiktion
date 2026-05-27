package dev.s7a.fiktion.generators

import dev.s7a.fiktion.runtime.FakeContext

private val DefaultStringLength: IntRange = 8..16

/**
 * Generates a string with a random length from [length] using [charset].
 */
public fun FakeContext.string(
    length: IntRange = DefaultStringLength,
    charset: FiktionCharset = FiktionCharsets.AlphaNumeric,
): String = throw NotImplementedError("String helper generation is not implemented yet.")

/**
 * Generates a string with [length] characters using [charset].
 */
public fun FakeContext.string(
    length: Int,
    charset: FiktionCharset = FiktionCharsets.AlphaNumeric,
): String = throw NotImplementedError("String helper generation is not implemented yet.")
