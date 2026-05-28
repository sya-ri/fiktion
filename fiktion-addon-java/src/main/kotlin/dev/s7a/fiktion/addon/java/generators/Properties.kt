package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.FiktionCharset
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.string
import java.util.Properties

/**
 * Generates Java properties.
 */
public fun FakeContext.properties(): Properties =
    Properties().apply {
        repeat(int(1..DEFAULT_PROPERTIES_SIZE)) {
            setProperty(
                string(length = DEFAULT_PROPERTY_KEY_LENGTH, charset = FiktionCharset.LowercaseAlphaNumeric),
                string(),
            )
        }
    }

private const val DEFAULT_PROPERTIES_SIZE: Int = 3
private const val DEFAULT_PROPERTY_KEY_LENGTH: Int = 12
