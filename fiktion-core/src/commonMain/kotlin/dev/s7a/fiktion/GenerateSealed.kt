@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates a sealed subtype value from registered [metadata].
 */
internal fun generateSealed(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    context: FakeContext,
    metadata: FiktionSealedMetadata<*>,
    exclusions: GenerationExclusions = GenerationExclusions(),
): Any? {
    requireNoValueExclusions(exclusions, "sealed generation")
    if (metadata.subtypes.isEmpty()) {
        throw CannotGenerateException(emptySealedMetadataMessage(request.type))
    }

    val subtypes = metadata.subtypes.filterNot { subtype -> exclusions.excludesType(subtype) }
    if (subtypes.isEmpty()) {
        throw FiktionConfigurationException("Cannot generate ${request.type} because all sealed subtypes were excluded.")
    }

    val subtype = subtypes[context.random.nextInt(subtypes.size)]
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
