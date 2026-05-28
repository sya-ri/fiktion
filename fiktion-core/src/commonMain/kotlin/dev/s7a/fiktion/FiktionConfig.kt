@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Immutable rule configuration used by a Fiktion generator.
 */
internal data class FiktionConfig(
    /**
     * Root seed used when a call does not provide one.
     */
    val seed: Long? = null,
    /**
     * Add-ons installed into this configuration. Their rules stay below explicit rules in precedence.
     */
    val addons: List<InstalledAddon> = emptyList(),
    /**
     * Explicit rules configured by users for a global, isolated, or per-call scope.
     */
    val rules: List<DefaultGenerationSpec<*>> = emptyList(),
    /**
     * Type construction metadata keyed by stable type id.
     */
    val metadata: Map<String, FiktionTypeMetadata<*>> = emptyMap(),
) {
    /**
     * Returns this configuration with [other] applied as a [rulePrecedence] overlay.
     */
    fun overlaidBy(
        other: FiktionConfig,
        rulePrecedence: RulePrecedence,
    ): FiktionConfig =
        FiktionConfig(
            seed = other.seed ?: seed,
            addons =
                addons.filterNot { addon ->
                    other.addons.any { overlay -> overlay.id == addon.id }
                } + other.addons.map { addon -> addon.snapshot(precedence = RulePrecedence.ADDON) },
            rules =
                rules +
                    other.rules.map { rule ->
                        rule.snapshot(precedence = rulePrecedence)
                    },
            metadata = metadata + other.metadata,
        )

    /**
     * Returns a copy with rule precedence normalized from lowest to highest.
     */
    fun normalized(): FiktionConfig =
        FiktionConfig(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot(precedence = RulePrecedence.ADDON) },
            rules = rules.map { rule -> rule.snapshot(precedence = RulePrecedence.GLOBAL) },
            metadata = metadata,
        )

    /**
     * Returns rules in lookup order from lowest to highest precedence.
     */
    fun effectiveRules(): List<DefaultGenerationSpec<*>> = addons.flatMap { it.rules } + rules
}
