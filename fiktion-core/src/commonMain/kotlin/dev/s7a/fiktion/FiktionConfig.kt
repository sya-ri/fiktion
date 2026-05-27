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
    val rules: List<RegisteredRule<*>> = emptyList(),
) {
    /**
     * Returns this configuration with [other] applied as a higher-precedence overlay.
     */
    fun overlaidBy(other: FiktionConfig): FiktionConfig =
        FiktionConfig(
            seed = other.seed ?: seed,
            addons =
                addons.filterNot { addon ->
                    other.addons.any { overlay -> overlay.id == addon.id }
                } + other.addons.map { addon -> addon.snapshot(precedence = ADDON_PRECEDENCE) },
            rules =
                rules +
                    other.rules.map { rule ->
                        rule.snapshot(precedence = nextRulePrecedence())
                    },
        )

    /**
     * Returns a copy with rule precedence normalized from lowest to highest.
     */
    fun normalized(): FiktionConfig =
        FiktionConfig(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot(precedence = ADDON_PRECEDENCE) },
            rules = rules.map { rule -> rule.snapshot(precedence = 0) },
        )

    /**
     * Returns the precedence for the next explicit rule overlay.
     */
    private fun nextRulePrecedence(): Int = rules.maxOfOrNull { rule -> rule.precedence }?.plus(1) ?: 0

    /**
     * Returns rules in lookup order from lowest to highest precedence.
     */
    fun effectiveRules(): List<RegisteredRule<*>> = addons.flatMap { it.rules } + rules

    private companion object {
        /**
         * Precedence used for add-on rules below every explicit rule layer.
         */
        const val ADDON_PRECEDENCE: Int = -1
    }
}
