package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.long
import java.util.concurrent.atomic.LongAccumulator

/**
 * Generates a Java long accumulator.
 */
public fun FakeContext.longAccumulator(): LongAccumulator =
    LongAccumulator(Long::plus, 0L).apply {
        accumulate(long())
    }
