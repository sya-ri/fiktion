package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.childContext

/**
 * Generates a triple from [first], [second], and [third].
 */
public fun <First, Second, Third> FakeContext.triple(
    first: FakeContext.() -> First,
    second: FakeContext.() -> Second,
    third: FakeContext.() -> Third,
): Triple<First, Second, Third> =
    Triple(
        first(childContext(index = 0)),
        second(childContext(index = 1)),
        third(childContext(index = 2)),
    )
