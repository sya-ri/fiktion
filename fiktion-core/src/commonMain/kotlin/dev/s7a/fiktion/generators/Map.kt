package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.FiktionConfig
import dev.s7a.fiktion.TypeFamilyGenerationContext
import dev.s7a.fiktion.childContext
import dev.s7a.fiktion.random

/**
 * Generates a map using [key] and [value].
 */
public fun <K, V> FakeContext.map(
    size: Int = int(config(FiktionConfig.Map.size)),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): Map<K, V> =
    List(size) { index ->
        key(childContext(index, seedIndex = index * 2)) to
            value(childContext(index, seedIndex = index * 2 + 1))
    }.toMap()

/**
 * Generates a mutable map using [key] and [value].
 */
public fun <K, V> FakeContext.mutableMap(
    size: Int = int(config(FiktionConfig.Map.size)),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): MutableMap<K, V> = map(size = size, key = key, value = value).toMutableMap()

/**
 * Generates a map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.map(): Map<Any?, Any?> =
    List(config(FiktionConfig.Map.size).random(random)) { index ->
        fakeKey(index) to fakeValue(index)
    }.toMap()

/**
 * Generates a mutable map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.mutableMap(): MutableMap<Any?, Any?> = map().toMutableMap()
