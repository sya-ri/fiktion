package dev.s7a.fiktion

/**
 * Creates a child fake context for generator parts.
 */
internal fun FakeContext.childContext(index: Int): FakeContext =
    DefaultFakeContext(
        seed = seed.childSeed(index),
        type = type,
        property = property,
        path = path,
        depth = depth + 1,
        index = index,
    )
