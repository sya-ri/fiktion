package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.LinkedHashMap

/**
 * Generates a Java linked hash map using [key] and [value].
 */
public fun <K, V> FakeContext.linkedHashMap(
    size: Int = FiktionConfig.Map.size(),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): LinkedHashMap<K, V> = LinkedHashMap(map(size = size, key = key, value = value))

/**
 * Generates a Java linked hash map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.linkedHashMap(): LinkedHashMap<Any?, Any?> =
    linkedHashMap(
        size = FiktionConfig.Map.size(),
        key = {
            fakeKey(index)
        },
        value = {
            fakeValue(index)
        },
    )
