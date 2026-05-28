package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.int
import java.util.OptionalInt

/**
 * Generates a Java optional int.
 */
public fun FakeContext.optionalInt(): OptionalInt =
    if (boolean()) {
        OptionalInt.of(int())
    } else {
        OptionalInt.empty()
    }
