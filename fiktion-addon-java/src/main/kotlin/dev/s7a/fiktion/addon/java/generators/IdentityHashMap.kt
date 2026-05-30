package dev.s7a.fiktion.addon.java.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.generators.int
import dev.s7a.fiktion.generators.map
import java.util.IdentityHashMap

/**
 * Generates a Java identity hash map using [key] and [value].
 */
public fun <K, V> FakeContext.identityHashMap(
    size: Int = int(config(FiktionConfig.Map.size)),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): IdentityHashMap<K, V> = IdentityHashMap(map(size = size, key = key, value = value))

/**
 * Generates a Java identity hash map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.identityHashMap(): IdentityHashMap<Any?, Any?> =
    identityHashMap(
        size = int(config(FiktionConfig.Map.size)),
        key = {
            fake(argumentIndex = 0, seedIndex = index)
        },
        value = {
            fake(argumentIndex = 1, seedIndex = index)
        },
    )
