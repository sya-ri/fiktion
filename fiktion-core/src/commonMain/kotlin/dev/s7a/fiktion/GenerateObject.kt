@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random
import kotlin.reflect.KType

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
    val arguments = mutableListOf<FiktionObjectArgument>()
    metadata.properties.forEachIndexed { index, property ->
        val childRequest = request.child(property = property)
        val childSeed = seed.childSeed(index)
        val argument =
            if (property.usesDefault(config = config, request = childRequest, seed = childSeed)) {
                FiktionObjectDefault
            } else {
                val rule = config.selectRule(childRequest)
                val dependencyValues =
                    (rule as? DefaultDependentGenerationSpec<*>)
                        ?.resolveDependencyValues(
                            owner = request.type,
                            target = property,
                            properties = metadata.properties,
                            arguments = arguments,
                        )
                val valueSeed = if (property.hasDefault) childSeed.childSeed(DEFAULTABLE_VALUE_SEED_INDEX) else childSeed
                FiktionObjectValue(
                    try {
                        generateValue(
                            request = childRequest,
                            config = config,
                            seed = valueSeed,
                            depth = depth + 1,
                            dependencyValues = dependencyValues,
                        )
                    } catch (cause: CannotGenerateException) {
                        throw CannotGenerateException(
                            message = objectArgumentGenerationMessage(type = request.type, property = property),
                            cause = cause,
                        )
                    },
                )
            }
        arguments += argument
    }

    return metadata.construct(arguments)
}

/**
 * Resolves dependency values from arguments generated earlier for the same object.
 */
private fun DefaultDependentGenerationSpec<*>.resolveDependencyValues(
    owner: KType,
    target: FiktionObjectProperty,
    properties: List<FiktionObjectProperty>,
    arguments: List<FiktionObjectArgument>,
): List<Any?> =
    dependencies.map { dependency ->
        if (dependency.owner != owner) {
            throw CannotGenerateException(unknownDependencyPropertyMessage(owner = owner, target = target, dependency = dependency))
        }
        val dependencyIndex = properties.indexOfFirst { property -> property.name == dependency.name }
        if (dependencyIndex == -1) {
            throw CannotGenerateException(unknownDependencyPropertyMessage(owner = owner, target = target, dependency = dependency))
        }
        val dependencyProperty = properties[dependencyIndex]
        if (dependency.value != null && dependencyProperty.type != dependency.value) {
            throw CannotGenerateException(
                dependencyTypeMismatchMessage(
                    owner = owner,
                    target = target,
                    dependency = dependency,
                    property = dependencyProperty,
                ),
            )
        }
        if (dependencyIndex >= arguments.size) {
            throw CannotGenerateException(dependencyOrderMessage(owner = owner, target = target, dependency = dependency))
        }

        when (val argument = arguments[dependencyIndex]) {
            FiktionObjectDefault -> {
                throw CannotGenerateException(dependencyDefaultValueMessage(owner = owner, target = target, dependency = dependency))
            }

            is FiktionObjectValue -> {
                argument.value
            }
        }
    }

/**
 * Returns whether [property] should use its constructor default for this generation.
 */
private fun FiktionObjectProperty.usesDefault(
    config: FiktionConfigState,
    request: GenerationRequest,
    seed: Long,
): Boolean {
    val rule = config.selectRule(request)
    if (!hasDefault) {
        if (rule?.defaultGenerates == true) {
            throw CannotGenerateException(missingDefaultValueMessage(type = request.type, property = this))
        }
        return false
    }

    if (rule?.defaultGenerates == true) return true

    if (rule != null && rule.precedence >= RulePrecedence.GLOBAL && rule.defaultProbability == null) return false
    val probabilityRule = rule?.takeIf { selectedRule -> selectedRule.defaultProbability != null }
    val defaultProbability = probabilityRule?.defaultProbability?.value ?: DEFAULT_CONSTRUCTOR_DEFAULT_PROBABILITY
    return Random(probabilityRule?.seed ?: seed).nextDouble() < defaultProbability
}

/**
 * Returns a request for generating [property] as a child of this request.
 */
private fun GenerationRequest.child(property: FiktionObjectProperty): GenerationRequest =
    GenerationRequest(
        type = property.type,
        owner = type,
        propertyName = property.name,
        containerParts = containerParts,
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
private const val DEFAULTABLE_VALUE_SEED_INDEX: Int = 0
private const val DEFAULT_CONSTRUCTOR_DEFAULT_PROBABILITY: Double = 0.5
