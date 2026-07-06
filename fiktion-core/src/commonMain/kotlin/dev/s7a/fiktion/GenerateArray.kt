@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random

/**
 * Generates an array from registered construction [metadata].
 */
internal fun generateArray(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    metadata: FiktionArrayMetadata<*>,
): Any {
    val count =
        config
            .selectCandidates(
                min = FiktionConfig.Array.minSize,
                max = FiktionConfig.Array.maxSize,
                excluding = FiktionConfig.Array.excludingSizes,
                request = request,
            ).sample(random = Random(seed))
    val elements =
        List(count) { index ->
            generateValue(
                request = GenerationRequest(type = metadata.elementType),
                config = config,
                seed = seed.childSeed(index),
                depth = depth + 1,
                dependencyValues = null,
            )
        }

    return metadata.construct(elements) as Any
}
