package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates a map using [state].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Key, Value, MapType : Map<Key, Value>> FakeContext.generateMap(
    request: GenerationRequest,
    spec: DefaultMapGenerationSpec<Key, Value, MapType>,
    config: FiktionConfigState,
): MapType {
    val count = spec.sizeRange.random(random)
    val entries =
        List(count) { index ->
            spec.entryGenerator?.let { generator ->
                return@List generator(childContext(index = index))
            }

            val key =
                spec.keyGenerator?.invoke(childContext(index = index * 2))
                    ?: generateAutomaticMapPart(
                        part = "keys",
                        type = spec.keyType,
                        config = config,
                        seed = seed.childSeed(index * 2),
                        depth = depth + 1,
                    )
            val value =
                spec.valueGenerator?.invoke(childContext(index = index * 2 + 1))
                    ?: generateAutomaticMapPart(
                        part = "values",
                        type = spec.valueType,
                        config = config,
                        seed = seed.childSeed(index * 2 + 1),
                        depth = depth + 1,
                    )
            key to value
        }

    return config.selectMapConverter(request)?.convert?.invoke(entries) as? MapType
        ?: missingMapConverter(request)
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
 * Reports that no map converter was configured for [request].
 */
private fun missingMapConverter(request: GenerationRequest): Nothing =
    throw CannotGenerateException("Cannot generate ${request.type} because no map converter is configured.")

/**
 * Generates a missing key or value by using the normal value generation pipeline.
 */
private fun generateAutomaticMapPart(
    part: String,
    type: KType?,
    config: FiktionConfigState,
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
