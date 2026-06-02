package dev.s7a.fiktion.addon.arrow.core.generators

import arrow.core.NonEmptySet
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generateUniqueElements
import dev.s7a.fiktion.generators.int

/**
 * Generates an Arrow non-empty set using [element].
 */
public fun <T> FakeContext.nonEmptySet(
    size: Int = int(config(FiktionConfig.Collection.size)).coerceAtLeast(1),
    element: FakeContext.() -> T,
): NonEmptySet<T> {
    require(size >= 1) { "size must be at least 1." }
    val elements =
        generateUniqueElements(
            size = size,
            strategy = config(FiktionConfig.Collection.uniqueElementStrategy),
        ) { index ->
            element(childContext(index = index))
        }
    return elements.toNonEmptySet()
}

/**
 * Generates an Arrow non-empty set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.nonEmptySet(): NonEmptySet<Any?> {
    val elements =
        generateUniqueElements(
            size = int(config(FiktionConfig.Collection.size)).coerceAtLeast(1),
            strategy = config(FiktionConfig.Collection.uniqueElementStrategy),
        ) { index ->
            fakeElement(index)
        }
    return elements.toNonEmptySet()
}

internal fun <T> List<T>.toNonEmptySet(): NonEmptySet<T> =
    NonEmptySet(
        first = first(),
        rest = drop(1),
    )
