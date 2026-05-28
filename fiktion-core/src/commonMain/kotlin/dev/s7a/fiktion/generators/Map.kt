package dev.s7a.fiktion.generators

import dev.s7a.fiktion.FakeContext

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

private const val MAP_ENTRY_PARTS = 2
