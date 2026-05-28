package dev.s7a.fiktion.generators

import dev.s7a.fiktion.DEFAULT_COLLECTION_SIZE_RANGE
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext

/**
 * Generates a list using [element].
 */
public fun <T> FakeContext.list(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): List<T> =
    List(size) { index ->
        element(childContext(index = index))
    }

/**
 * Generates a mutable list using [element].
 */
public fun <T> FakeContext.mutableList(
    size: Int = int(1..3),
    element: FakeContext.() -> T,
): MutableList<T> = list(size = size, element = element).toMutableList()

/**
 * Generates a list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.list(): List<Any?> =
    List(DEFAULT_COLLECTION_SIZE_RANGE.random(random)) { index ->
        fake(argumentIndex = 0, seedIndex = index)
    }

/**
 * Generates a mutable list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.mutableList(): MutableList<Any?> = list().toMutableList()
