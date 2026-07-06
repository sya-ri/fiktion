package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.HashMap

/**
 * Generates a Java hash map using [key] and [value].
 */
public fun <K, V> FakeContext.hashMap(
    size: Int = FiktionConfig.Map.size(),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): HashMap<K, V> = HashMap(map(size = size, key = key, value = value))

/**
 * Generates a Java hash map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.hashMap(): HashMap<Any?, Any?> =
    hashMap(
        size = FiktionConfig.Map.size(),
        key = {
            fakeKey(index)
        },
        value = {
            fakeValue(index)
        },
    )
