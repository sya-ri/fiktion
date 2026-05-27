@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Generates an object from registered construction [metadata].
 */
internal fun generateObject(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    metadata: FiktionObjectMetadata<*>,
): Any? {
    val values =
        metadata.properties.mapIndexed { index, property ->
            generateValue(
                request =
                    request.child(
                        property = property,
                    ),
                config = config,
                seed = seed.childSeed(index),
                depth = depth + 1,
            )
        }

    return metadata.construct(values)
}

/**
 * Returns a request for generating [property] as a child of this request.
 */
private fun GenerationRequest.child(property: FiktionObjectProperty): GenerationRequest =
    GenerationRequest(
        type = property.type,
        owner = type,
        propertyName = property.name,
        pathSegments =
            pathSegments +
                PathRuleSegment(
                    ownerId = type.nonNullTypeId(),
                    name = property.name,
                    valueId = property.type.nonNullTypeId(),
                ),
    )

/**
 * Derives a stable child seed for the constructor property at [index].
 */
private fun Long.childSeed(index: Int): Long = this xor ((index + 1).toLong() * CHILD_SEED_STEP)

/**
 * Odd constant used to spread deterministic child seeds.
 */
private const val CHILD_SEED_STEP: Long = -7046029254386353131L
