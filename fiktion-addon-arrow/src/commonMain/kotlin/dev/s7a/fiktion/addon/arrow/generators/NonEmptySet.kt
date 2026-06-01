package dev.s7a.fiktion.addon.arrow.generators

import arrow.core.NonEmptySet
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generators.int

/**
 * Generates an Arrow non-empty set using [element].
 */
public fun <T> FakeContext.nonEmptySet(
    size: Int = maxOf(1, int(config(FiktionConfig.Collection.size))),
    element: FakeContext.() -> T,
): NonEmptySet<T> {
    require(size >= 1) { "size must be at least 1." }
    return NonEmptySet(
        first = element(childContext(index = 0)),
        rest = List(size - 1) { index -> element(childContext(index = index + 1)) },
    )
}

/**
 * Generates an Arrow non-empty set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.nonEmptySet(): NonEmptySet<Any?> {
    val size = maxOf(1, int(config(FiktionConfig.Collection.size)))
    return NonEmptySet(
        first = fakeElement(0),
        rest = List(size - 1) { index -> fakeElement(index + 1) },
    )
}
