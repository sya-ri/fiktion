package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.generators.boolean
import dev.s7a.fiktion.generators.int
import java.util.BitSet

/**
 * Generates a Java bit set.
 */
public fun FakeContext.bitSet(): BitSet =
    BitSet().apply {
        repeat(int(1..DEFAULT_BITSET_SIZE)) {
            set(int(0 until DEFAULT_BITSET_SIZE), boolean())
        }
    }

private const val DEFAULT_BITSET_SIZE: Int = 128
