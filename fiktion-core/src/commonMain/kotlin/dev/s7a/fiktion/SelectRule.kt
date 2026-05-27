package dev.s7a.fiktion

/**
 * Selects the effective rule for [request] from this configuration.
 */
internal fun FiktionConfig.selectRule(request: GenerationRequest): RegisteredRule<*>? =
    effectiveRules()
        .withIndex()
        .filter { (_, rule) -> rule.matcher.matches(request) }
        .maxWithOrNull(
            compareBy<IndexedValue<RegisteredRule<*>>> { (_, rule) -> rule.precedence }
                .thenBy { (_, rule) -> rule.matcher.specificity }
                .thenBy { (index, _) -> index },
        )?.value
