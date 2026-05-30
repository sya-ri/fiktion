package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.concurrent.ConcurrentSkipListMap

/**
 * Generates a Java concurrent skip list map using [key] and [value].
 */
public fun <K : Any, V : Any> FakeContext.concurrentSkipListMap(
    size: Int = int(config(FiktionConfig.Map.size)),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): ConcurrentSkipListMap<K, V> =
    ConcurrentSkipListMap<K, V>(compareBy { mapKey -> mapKey.toString() }).apply {
        putAll(map(size = size, key = key, value = value))
    }

/**
 * Generates a Java concurrent skip list map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.concurrentSkipListMap(): ConcurrentSkipListMap<Any, Any> =
    ConcurrentSkipListMap<Any, Any>(compareBy { value -> value.toString() }).apply {
        map(
            size = int(config(FiktionConfig.Map.size)),
            key = {
                fakeKey(index)
            },
            value = {
                fakeValue(index)
            },
        ).forEach { (key, value) ->
            if (key == null || value == null) return@forEach
            put(key, value)
        }
    }
