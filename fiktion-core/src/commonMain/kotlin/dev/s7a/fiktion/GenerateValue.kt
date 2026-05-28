@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Null probability used when a nullable rule does not declare one explicitly.
 */
private const val DEFAULT_NULL_PROBABILITY = 0.5

/**
 * Generates a single value for [request] using [config].
 */
@Suppress("DEPRECATION")
internal fun generateValue(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
): Any? {
    val rule = config.selectRule(request)

    val contextSeed = rule?.seed ?: seed
    val path = request.toFakePath()
    val context =
        FakeContext(
            seed = contextSeed,
            type = request.type.toFakeType(),
            property = path.segments.lastOrNull(),
            path = path,
            depth = depth,
        )

    if (rule != null) {
        if (request.type.isMarkedNullable) {
            val nullProbability = rule.nullProbability?.value ?: DEFAULT_NULL_PROBABILITY
            if (context.random.nextDouble() < nullProbability) return null
        }

        if (rule.automaticallyGenerates) {
            rule.autoCollectionElementType?.let { elementType ->
                return generateAutomaticCollection(
                    request = request,
                    config = config,
                    seed = contextSeed,
                    depth = depth,
                    context = context,
                    elementType = elementType,
                    sizeRange = rule.autoCollectionSizeRange,
                )
            }

            return generateAutomaticValue(
                request = request,
                config = config,
                seed = contextSeed,
                depth = depth,
                context = context,
            )
        }

        if (rule is DefaultMapGenerationSpec<*, *, *>) {
            @Suppress("UNCHECKED_CAST")
            return context.generateMap(
                spec = rule as DefaultMapGenerationSpec<Any?, Any?, Map<Any?, Any?>>,
                config = config,
            )
        }

        if (rule is DefaultTypeFamilyGenerationSpec<*>) {
            return rule.generate(
                TypeFamilyGenerationContext(
                    context = context,
                    type = request.type,
                    config = config,
                ),
            )
        }

        return rule.generator(context)
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
 * Generates a value without applying the already selected explicit rule.
 */
internal fun generateAutomaticValue(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
    context: FakeContext,
): Any? {
    generateContainer(type = request.type, context = context, config = config)?.let { value ->
        return value
    }

    when (val metadata = config.metadata[request.type.nonNullTypeId()]) {
        is FiktionObjectMetadata<*> -> {
            return generateObject(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionArrayMetadata<*> -> {
            return generateArray(
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionValueMetadata<*> -> {
            return generateValueClass(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        is FiktionEnumMetadata<*> -> {
            return generateEnum(
                request = request,
                context = context,
                metadata = metadata,
            )
        }

        is FiktionSealedMetadata<*> -> {
            return generateSealed(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                context = context,
                metadata = metadata,
            )
        }

        null -> {
            Unit
        }
    }

    return generateBuiltIn(
        type = request.type,
        context = context,
        config = config,
    )
}
