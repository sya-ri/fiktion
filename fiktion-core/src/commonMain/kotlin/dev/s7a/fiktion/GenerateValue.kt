@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random

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
            random = Random(contextSeed),
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

        return rule.generator(context)
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

        is FiktionValueMetadata<*> -> {
            return generateValueClass(
                request = request,
                config = config,
                seed = seed,
                depth = depth,
                metadata = metadata,
            )
        }

        null -> {
            Unit
        }
    }

    return generateBuiltIn(request.type, context)
}
