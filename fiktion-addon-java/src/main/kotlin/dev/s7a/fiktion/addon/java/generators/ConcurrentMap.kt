package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import java.util.concurrent.ConcurrentMap

/**
 * Generates a Java concurrent map using [key] and [value].
 */
public fun <K : Any, V : Any> FakeContext.concurrentMap(
    size: Int = FiktionConfig.Map.size(),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): ConcurrentMap<K, V> = concurrentHashMap(size = size, key = key, value = value)

/**
 * Generates a Java concurrent map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.concurrentMap(): ConcurrentMap<Any, Any> = concurrentHashMap()
