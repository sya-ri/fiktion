@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates a value-like type from registered construction [metadata].
 */
internal fun generateValueClass(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    metadata: FiktionValueMetadata<*>,
): Any? {
    val value =
        generateValue(
            request =
                GenerationRequest(
                    type = metadata.underlyingType,
                    owner = request.type,
                    propertyName = request.propertyName,
                    pathSegments = request.pathSegments,
                ),
            config = config,
            seed = seed.childSeed(index = 0),
            depth = depth + 1,
        )

    return metadata.construct(value)
}
