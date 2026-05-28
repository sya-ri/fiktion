package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.double
import java.util.OptionalDouble

/**
 * Generates a Java optional double.
 */
public fun FakeContext.optionalDouble(): OptionalDouble =
    if (boolean()) {
        OptionalDouble.of(double())
    } else {
        OptionalDouble.empty()
    }
