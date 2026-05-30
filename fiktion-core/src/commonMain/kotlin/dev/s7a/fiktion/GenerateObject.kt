@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random

/**
 * Default-value probability used when a defaultable property rule does not declare one explicitly.
 */
private const val DEFAULT_VALUE_PROBABILITY = 0.5

/**
 * Generates an object from registered construction [metadata].
 */
internal fun generateObject(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    metadata: FiktionObjectMetadata<*>,
): Any? {
    val arguments =
        metadata.properties.mapIndexed { index, property ->
            val childRequest = request.child(property = property)
            val childSeed = seed.childSeed(index)
            if (property.usesDefault(config = config, request = childRequest, seed = childSeed)) {
                FiktionObjectDefault
            } else {
                FiktionObjectValue(
                    try {
                        generateValue(
                            request = childRequest,
                            config = config,
                            seed = childSeed,
                            depth = depth + 1,
                        )
                    } catch (cause: CannotGenerateException) {
                        throw CannotGenerateException(
                            message = objectArgumentGenerationMessage(type = request.type, property = property),
                            cause = cause,
                        )
                    },
                )
            }
        }

    return metadata.construct(arguments)
}

/**
 * Returns whether [property] should use its constructor default for this generation.
 */
private fun FiktionObjectProperty.usesDefault(
    config: FiktionConfigState,
    request: GenerationRequest,
    seed: Long,
): Boolean {
    if (!hasDefault) return false

    val rule = config.selectRule(request)
    val defaultProbability = rule?.defaultProbability?.value ?: DEFAULT_VALUE_PROBABILITY
    return Random(rule?.seed ?: seed).nextDouble() < defaultProbability
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
internal fun Long.childSeed(index: Int): Long = this xor ((index + 1).toLong() * CHILD_SEED_STEP)

/**
 * Odd constant used to spread deterministic child seeds.
 */
private const val CHILD_SEED_STEP: Long = -7046029254386353131L
