package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.NavigableSet

/**
 * Generates a Java navigable set using [element].
 */
public fun <T : Any> FakeContext.navigableSet(
    size: Int = int(config(FiktionConfig.Collection.size)),
    element: FakeContext.() -> T,
): NavigableSet<T> = treeSet(size = size, element = element)

/**
 * Generates a Java navigable set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.navigableSet(): NavigableSet<Any> = treeSet()
