@file:OptIn(dev.s7a.fiktion.ExperimentalFiktionApi::class)

package dev.s7a.fiktion


/**
 * Generates an enum value from registered [metadata].
 */
internal fun generateEnum(
    request: GenerationRequest,
    context: FakeContext,
    metadata: FiktionEnumMetadata<*>,
): Any {
    if (metadata.entries.isEmpty()) {
        throw CannotGenerateException("Cannot generate ${request.type} because the registered enum metadata has no entries.")
    }

    return metadata.entries[context.random.nextInt(metadata.entries.size)]
}
