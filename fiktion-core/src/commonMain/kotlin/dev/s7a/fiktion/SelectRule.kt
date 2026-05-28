package dev.s7a.fiktion

/**
 * Selects the effective rule for [request] from this configuration.
 */
internal fun FiktionConfig.selectRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
    effectiveRules()
        .withIndex()
        .filter { (_, rule) -> rule.matcher.matches(request) }
        .maxWithOrNull(
            compareBy<IndexedValue<DefaultGenerationSpec<*>>> { (_, rule) -> rule.precedence }
                .thenBy { (_, rule) -> rule.matcher.specificity }
                .thenBy { (index, _) -> index },
        )?.value
