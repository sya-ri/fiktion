@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates a value-like type from registered construction [metadata].
 */
internal fun generateValueClass(
    request: GenerationRequest,
    config: FiktionConfigState,
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
                        owner = metadata.type,
                        propertyName = metadata.propertyName,
                        pathSegments =
                            request.pathSegments +
                                PathRuleSegment(
                                    ownerId = metadata.type.nonNullTypeId(),
                                    name = metadata.propertyName,
                                    valueId = metadata.underlyingType.nonNullTypeId(),
                                ),
                    ),
                config = config,
                seed = seed.childSeed(index = 0),
                depth = depth + 1,
                dependencyValues = null,
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
