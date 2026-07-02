@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.reflect.KType

/**
 * Generates a single value for [request] using [config].
 */
internal fun generateValue(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    dependencyValues: List<Any?>? = null,
): Any? {
    val rule = config.selectRule(request)

    val contextSeed = rule?.seed ?: seed
    val context = request.toFakeContext(config = config, seed = contextSeed, depth = depth)

    if (rule != null) {
        return generateFromRule(
            rule = rule,
            request = request,
            config = config,
            contextSeed = contextSeed,
            depth = depth,
            context = context,
            dependencyValues = dependencyValues,
        )
    }

    if (request.type.isMarkedNullable && context.random.nextDouble() < DEFAULT_NULL_PROBABILITY) {
        return null
    }

    if (request.type.isMarkedNullable) {
        config.effectiveRules().selectNullableNonNullRule(request)?.let { nullableNonNullRule ->
            val nullableNonNullSeed = nullableNonNullRule.seed ?: seed
            return generateFromRule(
                rule = nullableNonNullRule,
                request = request,
                config = config,
                contextSeed = nullableNonNullSeed,
                depth = depth,
                context = request.toFakeContext(config = config, seed = nullableNonNullSeed, depth = depth),
            )
        }
    }

    return generateAutomaticValue(
        request = request,
        config = config,
        seed = seed,
        depth = depth,
        context = context,
    )
}

/**
 * Returns the fake context for a generation request at [seed] and [depth].
 */
private fun GenerationRequest.toFakeContext(
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
): FakeContext {
    val path = toFakePath()
    return DefaultFakeContext(
        seed = seed,
        type = type.toFakeType(),
        property = path.segments.lastOrNull(),
        path = path,
        depth = depth,
        index = index,
        config = config,
        request = this,
    )
}

/**
 * Generates a value without applying the already selected explicit rule.
 */
internal fun generateAutomaticValue(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    context: FakeContext,
): Any? {
    config.metadata[request.type.nonNullTypeId()]?.let { metadata ->
        return generateFromMetadata(
            metadata = metadata,
            request = request,
            config = config,
            seed = seed,
            depth = depth,
            context = context,
        )
    }

    config.effectiveAutomaticRules().selectAutomaticRule(request)?.let { rule ->
        return generateFromRule(
            rule = rule,
            request = request,
            config = config,
            contextSeed = seed,
            depth = depth,
            context = context,
            dependencyValues = null,
        )
    }

    throw CannotGenerateException(missingGenerationMessage(request = request, config = config))
}

/**
 * Generates a value from registered construction [metadata].
 */
private fun generateFromMetadata(
    metadata: FiktionTypeMetadata<*>,
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    context: FakeContext,
): Any? =
    when (metadata) {
        is FiktionObjectMetadata<*> -> {
            generateObject(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionArrayMetadata<*> -> {
            generateArray(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionValueMetadata<*> -> {
            generateValueClass(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionEnumMetadata<*> -> {
            generateEnum(
                request = request,
                context = context,
                metadata = metadata,
            )
        }

        is FiktionSealedMetadata<*> -> {
            generateSealed(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                context = context,
                metadata = metadata,
            )
        }
    }

/**
 * Generates a value from an already selected [rule].
 */
private fun generateFromRule(
    rule: DefaultGenerationSpec<*>,
    request: GenerationRequest,
    config: FiktionConfigState,
    contextSeed: Long,
    depth: Int,
    context: FakeContext,
    dependencyValues: List<Any?>?,
): Any? {
    if (request.type.isMarkedNullable) {
        rule.nullProbability?.let { nullProbability ->
            if (context.random.nextDouble() < nullProbability.value) return null
        }
    }

    if (rule.defaultGenerates) {
        throw CannotGenerateException(defaultValueWithoutConstructorArgumentMessage(request.type))
    }

    if (rule.automaticallyGenerates) {
        generateAutomaticContainerValue(
            request = request,
            config = config,
            seed = contextSeed,
            depth = depth,
            context = context,
        )?.let { value ->
            return value
        }

        return generateAutomaticValue(
            request = request,
            config = config,
            seed = contextSeed,
            depth = depth,
            context = context,
        )
    }

    if (rule is DefaultDependentGenerationSpec<*>) {
        return rule.generate(
            context = context,
            values =
                dependencyValues
                    ?: throw CannotGenerateException(dependencyRuleWithoutObjectContextMessage(request = request)),
        )
    }

    if (rule is DefaultTypeFamilyGenerationSpec<*>) {
        return rule.generate(
            TypeFamilyGenerationContext(
                context = context,
                requestedType = request.type,
                config = config,
                request = request,
            ),
        )
    }

    return rule.generator(context)
}

private const val DEFAULT_NULL_PROBABILITY: Double = 0.5

/**
 * Generates collection or map values when an exact `generates auto` rule targets a configured converter type.
 */
private fun generateAutomaticContainerValue(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    context: FakeContext,
): Any? {
    config.selectCollectionConverter(request)?.let { converter ->
        val elementType = request.type.typeArgument(index = 0) ?: return null
        val size = context.config(FiktionConfig.Collection.size).random(context.random)
        val elements =
            if (converter.unique) {
                generateUniqueElements(
                    size = size,
                    strategy = context.config(FiktionConfig.Collection.uniqueElementStrategy),
                ) { index ->
                    generateCollectionElement(
                        request = request,
                        config = config,
                        seed = seed,
                        depth = depth,
                        elementType = elementType,
                        index = index,
                    )
                }
            } else {
                List(size) { index ->
                    generateCollectionElement(
                        request = request,
                        config = config,
                        seed = seed,
                        depth = depth,
                        elementType = elementType,
                        index = index,
                    )
                }
            }
        return converter.convert(elements)
    }

    config.selectMapConverter(request)?.let { converter ->
        val keyType = request.type.typeArgument(index = 0) ?: return null
        val valueType = request.type.typeArgument(index = 1) ?: return null
        val entries =
            List(context.config(FiktionConfig.Map.size).random(context.random)) { index ->
                generateValue(
                    request =
                        GenerationRequest(
                            type = keyType,
                            containerParts =
                                request.containerParts +
                                    ContainerPart(kind = ContainerPart.Kind.MapKey, container = request.type),
                            index = index,
                        ),
                    config = config,
                    seed = seed.childSeed(index * 2),
                    depth = depth + 1,
                ) to
                    generateValue(
                        request =
                            GenerationRequest(
                                type = valueType,
                                containerParts =
                                    request.containerParts +
                                        ContainerPart(kind = ContainerPart.Kind.MapValue, container = request.type),
                                index = index,
                            ),
                        config = config,
                        seed = seed.childSeed(index * 2 + 1),
                        depth = depth + 1,
                    )
            }
        return converter.convert(entries)
    }

    return null
}

private fun generateCollectionElement(
    request: GenerationRequest,
    config: FiktionConfigState,
    seed: Long,
    depth: Int,
    elementType: KType,
    index: Int,
): Any? =
    generateValue(
        request =
            GenerationRequest(
                type = elementType,
                containerParts =
                    request.containerParts +
                        ContainerPart(kind = ContainerPart.Kind.Collection, container = request.type),
                index = index,
            ),
        config = config,
        seed = seed.childSeed(index),
        depth = depth + 1,
    )
