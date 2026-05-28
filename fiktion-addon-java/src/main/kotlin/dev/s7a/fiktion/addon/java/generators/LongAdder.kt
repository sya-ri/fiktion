package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.util.concurrent.atomic.LongAdder

/**
 * Generates a Java long adder.
 */
public fun FakeContext.longAdder(): LongAdder =
    LongAdder().apply {
        add(long())
    }
