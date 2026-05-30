@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Immutable rule configuration used by a Fiktion generator.
 */
internal data class FiktionConfigState(
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
     * Explicit generator configuration configured by users for a global, isolated, or per-call scope.
     */
    val configs: List<DefaultConfigSpec<*>> = emptyList(),
    /**
     * Collection converters configured by users for a global, isolated, or per-call scope.
     */
    val collectionConverters: List<CollectionConverter> = emptyList(),
    /**
     * Map converters configured by users for a global, isolated, or per-call scope.
     */
    val mapConverters: List<MapConverter> = emptyList(),
    /**
     * Type construction metadata keyed by stable type id.
     */
    val metadata: Map<String, FiktionTypeMetadata<*>> = emptyMap(),
) {
    /**
     * Returns this configuration with [other] applied as a [rulePrecedence] overlay.
     */
    fun overlaidBy(
        other: FiktionConfigState,
        rulePrecedence: RulePrecedence,
    ): FiktionConfigState =
        FiktionConfigState(
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
            configs =
                configs +
                    other.configs.map { config ->
                        config.snapshot(precedence = rulePrecedence)
                    },
            collectionConverters = collectionConverters + other.collectionConverters,
            mapConverters = mapConverters + other.mapConverters,
            metadata = metadata + other.metadata,
        )

    /**
     * Returns a copy with rule precedence normalized from lowest to highest.
     */
    fun normalized(): FiktionConfigState =
        FiktionConfigState(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot(precedence = RulePrecedence.ADDON) },
            rules = rules.map { rule -> rule.snapshot(precedence = RulePrecedence.GLOBAL) },
            configs = configs.map { config -> config.snapshot(precedence = RulePrecedence.GLOBAL) },
            collectionConverters = collectionConverters,
            mapConverters = mapConverters,
            metadata = metadata,
        )

    /**
     * Returns rules in lookup order from lowest to highest precedence.
     */
    fun effectiveRules(): List<DefaultGenerationSpec<*>> =
        mergeWithAddons(
            builtIn = BUILT_IN_RULES,
            addonValues = { addon -> addon.rules },
            explicit = rules,
        )

    /**
     * Returns config values in lookup order from lowest to highest precedence.
     */
    fun effectiveConfigs(): List<DefaultConfigSpec<*>> =
        mergeWithAddons(
            builtIn = emptyList(),
            addonValues = { addon -> addon.configs },
            explicit = configs,
        )

    /**
     * Returns rules used by `generates auto`, excluding explicit user rules to avoid recursively selecting itself.
     */
    fun effectiveAutomaticRules(): List<DefaultGenerationSpec<*>> =
        mergeWithAddons(
            builtIn = BUILT_IN_RULES,
            addonValues = { addon -> addon.rules },
            explicit = emptyList(),
        ).filterNot { rule -> rule.automaticallyGenerates }

    /**
     * Returns collection converters in lookup order from lowest to highest precedence.
     */
    fun effectiveCollectionConverters(): List<CollectionConverter> =
        mergeWithAddons(
            builtIn = BUILT_IN_COLLECTION_CONVERTERS,
            addonValues = { addon -> addon.collectionConverters },
            explicit = collectionConverters,
        )

    /**
     * Returns map converters in lookup order from lowest to highest precedence.
     */
    fun effectiveMapConverters(): List<MapConverter> =
        mergeWithAddons(
            builtIn = BUILT_IN_MAP_CONVERTERS,
            addonValues = { addon -> addon.mapConverters },
            explicit = mapConverters,
        )

    /**
     * Merges built-in, add-on, and explicit configuration layers from lowest to highest precedence.
     */
    private fun <T> mergeWithAddons(
        builtIn: List<T>,
        addonValues: (InstalledAddon) -> List<T>,
        explicit: List<T>,
    ): List<T> = builtIn + addons.flatMap(addonValues) + explicit
}
