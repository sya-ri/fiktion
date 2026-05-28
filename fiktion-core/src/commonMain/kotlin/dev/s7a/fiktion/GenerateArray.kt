@file:OptIn(dev.s7a.fiktion.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random

/**
 * Creates a typed array from compiler-generated array metadata.
 */
@ExperimentalFiktionApi
public inline fun <reified T> generatedArray(elements: List<Any?>): Array<T> =
    Array(elements.size) { index -> elements[index] as T }

/**
 * Generates an array from registered construction [metadata].
 */
internal fun generateArray(
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    metadata: FiktionArrayMetadata<*>,
): Any {
    val count = DEFAULT_COLLECTION_SIZE_RANGE.random(Random(seed))
    val elements =
        List(count) { index ->
            generateValue(
                request = GenerationRequest(type = metadata.elementType),
                config = config,
                seed = seed.childSeed(index),
                depth = depth + 1,
            )
        }

    return metadata.construct(elements) as Any
}
