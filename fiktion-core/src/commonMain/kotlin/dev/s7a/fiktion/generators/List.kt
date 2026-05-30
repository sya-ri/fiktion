package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext

/**
 * Generates a list using [element].
 */
public fun <T> FakeContext.list(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): List<T> =
    List(size) { index ->
        element(childContext(index = index))
    }

/**
 * Generates a mutable list using [element].
 */
public fun <T> FakeContext.mutableList(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): MutableList<T> = list(size = size, element = element).toMutableList()

/**
 * Generates a list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.list(): List<Any?> =
    List(config(FiktionConfig.Collection.size).random(random)) { index ->
        fake(argumentIndex = 0, seedIndex = index)
    }

/**
 * Generates a mutable list from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.mutableList(): MutableList<Any?> = list().toMutableList()
