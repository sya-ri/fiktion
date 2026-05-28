package dev.s7a.fiktion.generators

import dev.s7a.fiktion.DEFAULT_MAP_SIZE_RANGE
import dev.s7a.fiktion.FakeContext
import dev.s7a.fiktion.TypeFamilyGenerationContext

/**
 * Generates a map using [key] and [value].
 */
public fun <K, V> FakeContext.map(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): Map<K, V> =
    List(size) { index ->
        key(childContext(index = index * MAP_ENTRY_PARTS)) to value(childContext(index = index * MAP_ENTRY_PARTS + 1))
    }.toMap()

/**
 * Generates a mutable map using [key] and [value].
 */
public fun <K, V> FakeContext.mutableMap(
    size: Int = int(1..3),
    key: FakeContext.() -> K,
    value: FakeContext.() -> V,
): MutableMap<K, V> = map(size = size, key = key, value = value).toMutableMap()

/**
 * Generates a map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.map(): Map<Any?, Any?> =
    List(DEFAULT_MAP_SIZE_RANGE.random(random)) { index ->
        fake(argumentIndex = 0, seedIndex = index * MAP_ENTRY_PARTS) to
            fake(argumentIndex = 1, seedIndex = index * MAP_ENTRY_PARTS + 1)
    }.toMap()

/**
 * Generates a mutable map from the first and second requested type arguments.
 */
internal fun TypeFamilyGenerationContext.mutableMap(): MutableMap<Any?, Any?> = map().toMutableMap()

private const val MAP_ENTRY_PARTS = 2
