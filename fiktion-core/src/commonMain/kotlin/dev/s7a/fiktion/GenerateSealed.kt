@file:OptIn(dev.s7a.fiktion.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates a sealed subtype value from registered [metadata].
 */
internal fun generateSealed(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    context: FakeContext,
    metadata: FiktionSealedMetadata<*>,
): Any? {
    if (metadata.subtypes.isEmpty()) {
        throw CannotGenerateException(emptySealedMetadataMessage(request.type))
    }

    val subtype = metadata.subtypes[context.random.nextInt(metadata.subtypes.size)]
    try {
        return generateValue(
            request =
                request.copy(
                    type = subtype,
                ),
            config = config,
            seed = seed.childSeed(index = 0),
            depth = depth + 1,
        )
    } catch (cause: CannotGenerateException) {
        throw CannotGenerateException(
            "Cannot generate ${request.type} because sealed metadata selected subtype $subtype, " +
                "but that subtype could not be generated. ${cause.message}",
            cause = cause,
        )
    }
}
