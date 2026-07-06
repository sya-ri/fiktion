package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.SortedSet

/**
 * Generates a Java sorted set using [element].
 */
public fun <T : Any> FakeContext.sortedSet(
    size: Int = FiktionConfig.Collection.size(),
    element: FakeContext.() -> T,
): SortedSet<T> = treeSet(size = size, element = element)

/**
 * Generates a Java sorted set from the first requested type argument.
 */
internal fun TypeFamilyGenerationContext.sortedSet(): SortedSet<Any> = treeSet()
