package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates a collection using [spec] and the converter configured for [request].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Element, CollectionType : Collection<Element>> FakeContext.generateCollection(
    request: GenerationRequest,
    config: FiktionConfig,
    spec: DefaultCollectionGenerationSpec<Element, CollectionType>,
): CollectionType {
    val count = spec.sizeRange.random(random)
    val elements =
        List(count) { index ->
            val elementSeed = seed.childSeed(index)
            spec.elementGenerator(
                DefaultFakeContext(
                    seed = elementSeed,
                    type = type,
                    property = property,
                    path = path,
                    depth = depth + 1,
                    index = index,
                ),
            )
        }

    return config.selectCollectionConverter(request)?.convert?.invoke(elements) as? CollectionType
        ?: missingCollectionConverter(request)
}

/**
 * Generates a collection by automatically generating each element with [elementType].
 */
@Suppress("UNCHECKED_CAST")
internal fun generateAutomaticCollection(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    context: FakeContext,
    elementType: KType,
    sizeRange: IntRange,
): Any {
    val count = sizeRange.random(context.random)
    val elements =
        List(count) { index ->
            generateValue(
                request = GenerationRequest(type = elementType),
                config = config,
                seed = seed.childSeed(index),
                depth = depth + 1,
            )
        }

    return config.selectCollectionConverter(request)?.convert?.invoke(elements)
        ?: missingCollectionConverter(request)
}

/**
 * Reports that no collection converter was configured for [request].
 */
private fun missingCollectionConverter(request: GenerationRequest): Nothing =
    throw CannotGenerateException("Cannot generate ${request.type} because no collection converter is configured.")
