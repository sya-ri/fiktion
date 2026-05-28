package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates a collection using [elementGenerator] and [size].
 */
@Suppress("UNCHECKED_CAST", "DEPRECATION")
internal fun <Element, CollectionType : Collection<Element>> FakeContext.generateCollection(
    sizeRange: IntRange,
    elementGenerator: FakeContext.() -> Element,
): CollectionType {
    val count = sizeRange.random(random)
    val elements =
        List(count) { index ->
            val elementSeed = seed.childSeed(index)
            elementGenerator(
                FakeContext(
                    seed = elementSeed,
                    type = type,
                    property = property,
                    path = path,
                    depth = depth + 1,
                ),
            )
        }

    return when {
        type.id.isSetTypeId() -> elements.toMutableSet()
        else -> elements.toMutableList()
    } as CollectionType
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

    return when {
        request.type.classifier == Set::class || request.type.classifier == MutableSet::class -> elements.toMutableSet()
        else -> elements.toMutableList()
    }
}

/**
 * Returns true when this type id represents a set-like collection.
 */
private fun String.isSetTypeId(): Boolean =
    startsWith("kotlin.collections.Set<") ||
    startsWith("kotlin.collections.MutableSet<") ||
        startsWith("java.util.Set<") ||
        startsWith("java.util.MutableSet<") ||
        startsWith("Set<") ||
        startsWith("MutableSet<")
