package dev.s7a.fiktion.addon.arrow.generators

import arrow.core.NonEmptyList
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generators.int

/**
 * Generates an Arrow non-empty list using [element].
 */
public fun <T> FakeContext.nonEmptyList(
    size: Int = maxOf(1, int(config(FiktionConfig.Collection.size))),
    element: FakeContext.() -> T,
): NonEmptyList<T> {
    require(size >= 1) { "size must be at least 1." }
    return NonEmptyList(
        head = element(childContext(index = 0)),
        tail = List(size - 1) { index -> element(childContext(index = index + 1)) },
    )
}

/**
 * Generates an Arrow non-empty list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.nonEmptyList(): NonEmptyList<Any?> {
    val size = maxOf(1, int(config(FiktionConfig.Collection.size)))
    return NonEmptyList(
        head = fakeElement(0),
        tail = List(size - 1) { index -> fakeElement(index + 1) },
    )
}
