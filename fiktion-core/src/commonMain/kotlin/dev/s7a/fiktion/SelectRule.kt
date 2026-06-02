package dev.s7a.fiktion

/**
 * Selects the effective rule for [request] from this configuration.
 */
internal fun FiktionConfigState.selectRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
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
 * Selects an automatic rule for [request], allowing built-in and add-on rules for non-null types to generate the
 * non-null side of nullable automatic generation.
 */
internal fun Iterable<DefaultGenerationSpec<*>>.selectAutomaticRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
    selectRule(request) ?: if (request.type.isMarkedNullable) selectNullableAutomaticRule(request) else null

private fun Iterable<DefaultGenerationSpec<*>>.selectNullableAutomaticRule(request: GenerationRequest): DefaultGenerationSpec<*>? =
    filter { rule -> rule.matcher.matchesNullableAutomaticRequest(request) }
        .fold(initial = null) { selected, candidate ->
            when {
                selected == null -> candidate
                candidate.hasHigherPriorityThan(selected) -> candidate
                else -> selected
            }
        }

private fun RuleMatcher.matchesNullableAutomaticRequest(request: GenerationRequest): Boolean =
    when (this) {
        is RuleMatcher.Type -> request.type.nonNullTypeId() == type.nonNullTypeId()
        is RuleMatcher.TypeFamily -> request.type.classifier == type.classifier
        else -> false
    }

/**
 * Selects the effective config value for [key] and [request].
 */
@Suppress("UNCHECKED_CAST")
internal fun <Value : Any> FiktionConfigState.selectConfig(
    key: FiktionConfig<*, Value>,
    request: GenerationRequest,
): Value =
    effectiveConfigs()
        .filter { config -> config.key == key && config.matcher.matches(request) }
        .fold(initial = null as DefaultConfigSpec<*>?) { selected, candidate ->
            when {
                selected == null -> candidate
                candidate.hasHigherPriorityThan(selected) -> candidate
                else -> selected
            }
        }?.value as? Value ?: key.defaultValue

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
 * Returns whether this config should override [other] within the effective lookup order.
 */
private fun DefaultConfigSpec<*>.hasHigherPriorityThan(other: DefaultConfigSpec<*>): Boolean =
    when {
        precedence != other.precedence -> precedence > other.precedence
        matcher.specificity != other.matcher.specificity -> matcher.specificity > other.matcher.specificity
        else -> true
    }

/**
 * Selects the effective collection converter for [request] from this configuration.
 */
internal fun FiktionConfigState.selectCollectionConverter(request: GenerationRequest): CollectionConverter? =
    effectiveCollectionConverters()
        .lastOrNull { converter -> converter.classifier == request.type.classifier }

/**
 * Selects the effective map converter for [request] from this configuration.
 */
internal fun FiktionConfigState.selectMapConverter(request: GenerationRequest): MapConverter? =
    effectiveMapConverters()
        .lastOrNull { converter -> converter.classifier == request.type.classifier }
