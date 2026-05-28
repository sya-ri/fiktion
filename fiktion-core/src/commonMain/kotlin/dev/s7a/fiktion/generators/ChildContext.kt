package dev.s7a.fiktion.generators

import dev.s7a.fiktion.DefaultFakeContext
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.childSeed

/**
 * Creates a child fake context for generator parts.
 */
@Suppress("DEPRECATION")
internal fun FakeContext.childContext(index: Int): FakeContext =
    DefaultFakeContext(
        seed = seed.childSeed(index),
        type = type,
        property = property,
        path = path,
        depth = depth + 1,
        index = index,
    )
