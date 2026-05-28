package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates a map using [state].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Key, Value, MapType : Map<Key, Value>> FakeContext.generateMap(
    spec: DefaultMapGenerationSpec<Key, Value, MapType>,
    config: FiktionConfig,
): MapType {
    val count = spec.sizeRange.random(random)
    val entries =
        List(count) { index ->
            spec.entryGenerator?.let { generator ->
                return@List generator(childContext(index = index))
            }

            val key =
                spec.keyGenerator?.invoke(childContext(index = index * MAP_ENTRY_PARTS))
                    ?: generateAutomaticMapPart(
                        part = "keys",
                        type = spec.keyType,
                        config = config,
                        seed = seed.childSeed(index * MAP_ENTRY_PARTS),
                        depth = depth + 1,
                    )
            val value =
                spec.valueGenerator?.invoke(childContext(index = index * MAP_ENTRY_PARTS + 1))
                    ?: generateAutomaticMapPart(
                        part = "values",
                        type = spec.valueType,
                        config = config,
                        seed = seed.childSeed(index * MAP_ENTRY_PARTS + 1),
                        depth = depth + 1,
                    )
            key to value
        }

    return entries.toMap().toMutableMap() as MapType
}

/**
 * Generates a map by automatically generating keys and values.
 */
internal fun generateAutomaticMap(
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    context: FakeContext,
    keyType: KType,
    valueType: KType,
    sizeRange: IntRange,
): Any {
    val count = sizeRange.random(context.random)
    val entries =
        List(count) { index ->
            val key =
                generateValue(
                    request = GenerationRequest(type = keyType),
                    config = config,
                    seed = seed.childSeed(index * MAP_ENTRY_PARTS),
                    depth = depth + 1,
                )
            val value =
                generateValue(
                    request = GenerationRequest(type = valueType),
                    config = config,
                    seed = seed.childSeed(index * MAP_ENTRY_PARTS + 1),
                    depth = depth + 1,
                )
            key to value
        }

    return entries.toMap().toMutableMap()
}

/**
 * Creates a child context for a generated map entry part.
 */
@Suppress("DEPRECATION")
private fun FakeContext.childContext(index: Int): FakeContext {
    val childSeed = seed.childSeed(index)
    return DefaultFakeContext(
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
 * Generates a missing key or value by using the normal value generation pipeline.
 */
private fun generateAutomaticMapPart(
    part: String,
    type: KType?,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
): Any? {
    if (type == null) missingMapPart(part)
    return generateValue(
        request = GenerationRequest(type = type),
        config = config,
        seed = seed,
        depth = depth,
    )
}

/**
 * Number of generated parts in a key/value map entry.
 */
internal const val MAP_ENTRY_PARTS: Int = 2
