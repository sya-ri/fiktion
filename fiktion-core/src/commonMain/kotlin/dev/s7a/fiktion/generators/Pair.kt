package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

/**
 * Generates a pair from [first] and [second].
 */
public fun <First, Second> FakeContext.pair(
    first: FakeContext.() -> First,
    second: FakeContext.() -> Second,
): Pair<First, Second> = first(childContext(index = 0)) to second(childContext(index = 1))
