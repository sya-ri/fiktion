package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.double
import java.util.concurrent.atomic.DoubleAdder

/**
 * Generates a Java double adder.
 */
public fun FakeContext.doubleAdder(): DoubleAdder =
    DoubleAdder().apply {
        add(double())
    }
