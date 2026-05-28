package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.concurrent.ConcurrentHashMap

/**
 * Generates a Java concurrent hash map using [key] and [value].
 */
public fun <K, V> FakeContext.concurrentHashMap(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): ConcurrentHashMap<K & Any, V & Any> =
    ConcurrentHashMap<K & Any, V & Any>().apply {
        map(size = size, key = key, value = value).forEach { (key, value) ->
            if (key != null && value != null) {
                put(key, value)
            }
        }
    }

/**
 * Generates a Java concurrent hash map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.concurrentHashMap(): ConcurrentHashMap<Any, Any> =
    concurrentHashMap(
        size = int(1..3),
        key = {
            fake(argumentIndex = 0, seedIndex = index)
        },
        value = {
            fake(argumentIndex = 1, seedIndex = index)
        },
    )
