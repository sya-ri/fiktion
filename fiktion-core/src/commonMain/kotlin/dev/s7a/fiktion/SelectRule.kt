package dev.s7a.fiktion

/**
 * Selects the effective rule for [request] from this configuration.
 */
internal fun FiktionConfig.selectRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
    effectiveRules()
        .selectRule(request)

/**
 * Selects the effective rule for [request] from this rule collection.
 */
internal fun Iterable<DefaultGenerationSpec<*>>.selectRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
    withIndex()
        .filter { (_, rule) -> rule.matcher.matches(request) }
        .maxWithOrNull(
            compareBy<IndexedValue<DefaultGenerationSpec<*>>> { (_, rule) -> rule.precedence }
                .thenBy { (_, rule) -> rule.matcher.specificity }
                .thenBy { (index, _) -> index },
        )?.value

/**
 * Selects the effective collection converter for [request] from this configuration.
 */
internal fun FiktionConfig.selectCollectionConverter(request: GenerationRequest): CollectionConverter? =
    effectiveCollectionConverters()
        .lastOrNull { converter -> converter.classifier == request.type.classifier }

/**
 * Selects the effective map converter for [request] from this configuration.
 */
internal fun FiktionConfig.selectMapConverter(request: GenerationRequest): MapConverter? =
    effectiveMapConverters()
        .lastOrNull { converter -> converter.classifier == request.type.classifier }
