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
    filter { rule -> rule.matcher.matches(request) }
        .fold(initial = null) { selected, candidate ->
            when {
                selected == null -> candidate
                candidate.hasHigherPriorityThan(selected) -> candidate
                else -> selected
            }
        }

/**
 * Returns whether this rule should override [other] within the effective lookup order.
 */
private fun DefaultGenerationSpec<*>.hasHigherPriorityThan(other: DefaultGenerationSpec<*>): Boolean =
    when {
        precedence != other.precedence -> precedence > other.precedence
        matcher.specificity != other.matcher.specificity -> matcher.specificity > other.matcher.specificity
        else -> true
    }

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
