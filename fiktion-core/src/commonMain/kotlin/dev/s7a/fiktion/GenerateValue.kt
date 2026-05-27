@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

import kotlin.random.Random

/**
 * Generates a single value for [request] using [config].
 */
internal fun generateValue(
    request: GenerationRequest,
    config: FiktionConfig,
    seed: Long,
    depth: Int,
): Any? {
    val rules = config.effectiveRules()
    val rule =
        rules
            .withIndex()
            .filter { (_, rule) -> rule.matcher.matches(request) }
            .maxWithOrNull(
                compareBy<IndexedValue<RegisteredRule<*>>> { (_, rule) -> rule.precedence }
                    .thenBy { (_, rule) -> rule.matcher.specificity }
                    .thenBy { (index, _) -> index },
            )?.value

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
        val nullProbability = rule.nullProbability
        if (request.type.isMarkedNullable && nullProbability != null && context.random.nextDouble() < nullProbability.value) return null

        return rule.generator(context)
    }

    val metadata = config.metadata[request.type.nonNullTypeId()]
    if (metadata != null) {
        return generateObject(
            request = request,
            config = config,
            seed = seed,
            depth = depth,
            metadata = metadata,
        )
    }

    return generateBuiltIn(request.type, context)
}
