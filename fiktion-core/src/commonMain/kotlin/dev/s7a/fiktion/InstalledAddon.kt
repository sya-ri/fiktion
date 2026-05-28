package dev.s7a.fiktion

/**
 * Installed add-on and the rules it contributed.
 */
internal data class InstalledAddon(
    /**
     * Stable add-on identifier.
     */
    val id: String,
    /**
     * Rules contributed by this add-on.
     */
    val rules: List<DefaultGenerationSpec<*>>,
    /**
     * Collection converters contributed by this add-on.
     */
    val collectionConverters: List<CollectionConverter> = emptyList(),
    /**
     * Map converters contributed by this add-on.
     */
    val mapConverters: List<MapConverter> = emptyList(),
) {
    /**
     * Returns a detached copy whose rules can be stored in an immutable configuration snapshot.
     */
    fun snapshot(precedence: RulePrecedence? = null): InstalledAddon =
        copy(rules = rules.map { rule -> rule.snapshot(precedence = precedence ?: rule.precedence) })
}
