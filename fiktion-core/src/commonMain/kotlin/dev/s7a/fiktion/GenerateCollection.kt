package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.FakeContext
import kotlin.random.Random

/**
 * Generates a collection using [elementGenerator] and [size].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Element, CollectionType : Collection<Element>> FakeContext.generateCollection(
    sizeRange: IntRange,
    elementGenerator: FakeContext.() -> Element,
): CollectionType {
    val count = sizeRange.random(random)
    val elements =
        List(count) { index ->
            val elementSeed = seed.childSeed(index)
            elementGenerator(
                DefaultFakeContext(
                    seed = elementSeed,
                    random = Random(elementSeed),
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
 * Returns true when this type id represents a set-like collection.
 */
private fun String.isSetTypeId(): Boolean = contains("Set<")
