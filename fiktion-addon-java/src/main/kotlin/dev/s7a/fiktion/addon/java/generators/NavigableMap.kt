package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.NavigableMap

/**
 * Generates a Java navigable map using [key] and [value].
 */
public fun <K : Any, V> FakeContext.navigableMap(
    size: Int = int(config(FiktionConfig.Map.size)),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): NavigableMap<K, V> = treeMap(size = size, key = key, value = value)

/**
 * Generates a Java navigable map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.navigableMap(): NavigableMap<Any, Any?> = treeMap()
