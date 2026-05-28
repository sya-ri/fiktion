package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.long
import java.util.OptionalLong

/**
 * Generates a Java optional long.
 */
public fun FakeContext.optionalLong(): OptionalLong =
    if (boolean()) {
        OptionalLong.of(long())
    } else {
        OptionalLong.empty()
    }
