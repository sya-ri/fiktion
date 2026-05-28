package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.double
import java.util.concurrent.atomic.DoubleAccumulator

/**
 * Generates a Java double accumulator.
 */
public fun FakeContext.doubleAccumulator(): DoubleAccumulator =
    DoubleAccumulator(Double::plus, 0.0).apply {
        accumulate(double())
    }
