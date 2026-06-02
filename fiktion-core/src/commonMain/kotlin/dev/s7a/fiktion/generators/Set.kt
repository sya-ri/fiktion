package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.generateUniqueElements
import dev.s7a.fiktion.random

/**
 * Generates a set using [element].
 */
public fun <T> FakeContext.set(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): Set<T> =
    generateUniqueElements(
        size = size,
        strategy = config(FiktionConfig.Collection.uniqueElementStrategy),
    ) { index ->
        element(childContext(index))
    }.toSet()

/**
 * Generates a mutable set using [element].
 */
public fun <T> FakeContext.mutableSet(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): MutableSet<T> = set(size = size, element = element).toMutableSet()

/**
 * Generates a set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.set(): Set<Any?> =
    generateUniqueElements(
        size = config(FiktionConfig.Collection.size).random(random),
        strategy = config(FiktionConfig.Collection.uniqueElementStrategy),
    ) { index ->
        fakeElement(index)
    }.toSet()

/**
 * Generates a mutable set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.mutableSet(): MutableSet<Any?> = set().toMutableSet()
