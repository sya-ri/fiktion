package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.SortedMap

/**
 * Generates a Java sorted map using [key] and [value].
 */
public fun <K : Any, V> FakeContext.sortedMap(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): SortedMap<K, V> = treeMap(size = size, key = key, value = value)

/**
 * Generates a Java sorted map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.sortedMap(): SortedMap<Any, Any?> = treeMap()
