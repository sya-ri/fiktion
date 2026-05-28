package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.oneOf
import dev.s7a.fiktion.generators.string
import java.util.StringJoiner

/**
 * Generates a Java string joiner containing random strings.
 */
public fun FakeContext.stringJoiner(): StringJoiner =
    StringJoiner(oneOf(STRING_JOINER_DELIMITERS)).apply {
        repeat(int(1..DEFAULT_STRING_JOINER_SIZE)) {
            add(string())
        }
    }

private val STRING_JOINER_DELIMITERS: List<String> = listOf(",", "|", ";", " ")
private const val DEFAULT_STRING_JOINER_SIZE: Int = 3
