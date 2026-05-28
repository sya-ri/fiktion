@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Null probability used when a nullable rule does not declare one explicitly.
 */
private const val DEFAULT_NULL_PROBABILITY = 0.5

/**
 * Generates a single value for [request] using [config].
 */
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
        DefaultFakeContext(
            seed = contextSeed,
            type = request.type.toFakeType(),
            property = path.segments.lastOrNull(),
            path = path,
            depth = depth,
            index = 0,
        )

    if (rule != null) {
        return generateFromRule(
            rule = rule,
            request = request,
            config = config,
            contextSeed = contextSeed,
            depth = depth,
            context = context,
        )
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

    config.effectiveAutomaticRules().selectRule(request)?.let { rule ->
        return generateFromRule(
            rule = rule,
            request = request,
            config = config,
            contextSeed = seed,
            depth = depth,
            context = context,
        )
    }

    throw CannotGenerateException(missingGenerationMessage(request.type))
}

/**
 * Generates a value from registered construction [metadata].
 */
private fun generateFromMetadata(
    metadata: FiktionTypeMetadata<*>,
    request: GenerationRequest,
    config: FiktionConfig,
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
    config: FiktionConfig,
    contextSeed: Long,
    depth: Int,
    context: FakeContext,
): Any? {
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
            request = request,
            spec = rule as DefaultMapGenerationSpec<Any?, Any?, Map<Any?, Any?>>,
            config = config,
        )
    }

    if (rule is DefaultCollectionGenerationSpec<*, *>) {
        @Suppress("UNCHECKED_CAST")
        return context.generateCollection(
            request = request,
            config = config,
            spec = rule as DefaultCollectionGenerationSpec<Any?, Collection<Any?>>,
        )
    }

    if (rule is DefaultTypeFamilyGenerationSpec<*>) {
        return rule.generate(
            TypeFamilyGenerationContext(
                context = context,
                requestedType = request.type,
                config = config,
            ),
        )
    }

    return rule.generator(context)
}
