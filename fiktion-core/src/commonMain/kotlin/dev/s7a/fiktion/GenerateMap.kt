package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.CannotGenerateException
import dev.s7a.fiktion.runtime.FakeContext

/**
 * Generates a map using [state].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Key, Value, MapType : Map<Key, Value>> FakeContext.generateMap(
    spec: DefaultMapGenerationSpec<Key, Value, MapType>,
): MapType {
    val count = spec.sizeRange.random(random)
    val entries =
        List(count) { index ->
            spec.entryGenerator?.let { generator ->
                return@List generator(childContext(index = index))
            }

            val key = spec.keyGenerator?.invoke(childContext(index = index * MAP_ENTRY_PARTS)) ?: missingMapPart("keys")
            val value =
                spec.valueGenerator?.invoke(childContext(index = index * MAP_ENTRY_PARTS + 1))
                    ?: missingMapPart("values")
            key to value
        }

    return entries.toMap().toMutableMap() as MapType
}

/**
 * Creates a child context for a generated map entry part.
 */
@Suppress("DEPRECATION")
private fun FakeContext.childContext(index: Int): FakeContext {
    val childSeed = seed.childSeed(index)
    return FakeContext(
        seed = childSeed,
        type = type,
        property = property,
        path = path,
        depth = depth + 1,
    )
}

/**
 * Reports that a map rule was generated before both key and value generation were configured.
 */
private fun missingMapPart(part: String): Nothing =
    throw CannotGenerateException(
        "Cannot generate map $part because the map rule is incomplete. " +
            "Configure entries with generatesEach, or configure both generatesKeys and generatesValues.",
    )

/**
 * Number of generated parts in a key/value map entry.
 */
private const val MAP_ENTRY_PARTS = 2
