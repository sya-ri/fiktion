@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates an enum value from registered [metadata].
 */
internal fun generateEnum(
    request: GenerationRequest,
    context: FakeContext,
    metadata: FiktionEnumMetadata<*>,
    exclusions: GenerationExclusions = GenerationExclusions(),
): Any {
    requireNoTypeExclusions(exclusions, "enum generation")
    if (metadata.entries.isEmpty()) {
        throw CannotGenerateException("Cannot generate ${request.type} because the registered enum metadata has no entries.")
    }

    val entries = metadata.entries.filterNot { entry -> exclusions.excludesValue(entry) }
    if (entries.isEmpty()) {
        throw FiktionConfigurationException("Cannot generate ${request.type} because all enum entries were excluded.")
    }

    return entries[context.random.nextInt(entries.size)]
}
