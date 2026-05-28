@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.CannotGenerateException

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
        try {
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
        } catch (cause: CannotGenerateException) {
            throw CannotGenerateException(
                message =
                    valueUnderlyingGenerationMessage(
                        type = request.type,
                        underlyingType = metadata.underlyingType,
                    ),
                cause = cause,
            )
        }

    return metadata.construct(value)
}
