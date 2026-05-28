package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.TreeMap

/**
 * Generates a Java tree map using [key] and [value].
 */
public fun <K, V> FakeContext.treeMap(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): TreeMap<K & Any, V> =
    TreeMap<K & Any, V>(compareBy { mapKey -> mapKey.toString() }).apply {
        map(size = size, key = key, value = value).forEach { (key, value) ->
            if (key != null) {
                put(key, value)
            }
        }
    }

/**
 * Generates a Java tree map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.treeMap(): TreeMap<Any, Any?> =
    treeMap(
        size = int(1..3),
        key = {
            fake(argumentIndex = 0, seedIndex = index)
        },
        value = {
            fake(argumentIndex = 1, seedIndex = index)
        },
    )
