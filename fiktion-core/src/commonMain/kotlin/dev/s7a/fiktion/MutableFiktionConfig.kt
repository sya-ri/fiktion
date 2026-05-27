@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Mutable builder-side representation of [FiktionConfig].
 */
internal class MutableFiktionConfig(
    seed: Long? = null,
    addons: List<InstalledAddon> = emptyList(),
    rules: List<RegisteredRule<*>> = emptyList(),
    metadata: Map<String, FiktionObjectMetadata<*>> = emptyMap(),
) {
    /**
     * Root seed configured for the resulting snapshot.
     */
    var seed: Long? = seed

    /**
     * Installed add-on buffer.
     */
    private val addons: MutableList<InstalledAddon> = addons.toMutableList()

    /**
     * Explicit rule buffer.
     */
    private val rules: MutableList<RegisteredRule<*>> = rules.toMutableList()

    /**
     * Object construction metadata keyed by stable type id.
     */
    private val metadata: MutableMap<String, FiktionObjectMetadata<*>> = metadata.toMutableMap()

    /**
     * Rule buffer for the add-on currently being installed.
     */
    private var installingAddonRules: MutableList<RegisteredRule<*>>? = null

    /**
     * Adds [rule], replacing an existing rule with the same key within the current rule layer.
     */
    fun add(rule: RegisteredRule<*>) {
        val targetRules = installingAddonRules ?: rules
        targetRules.removeAll { it.key == rule.key }
        targetRules += rule
    }

    /**
     * Registers [metadata], replacing existing metadata for the same generated type.
     */
    fun register(metadata: FiktionObjectMetadata<*>) {
        this.metadata[metadata.type.toString()] = metadata
    }

    /**
     * Runs [install] while collecting rules for the add-on identified by [id].
     */
    fun installAddon(
        id: String,
        install: () -> Unit,
    ) {
        val previousAddonRules = installingAddonRules
        val addonRules = mutableListOf<RegisteredRule<*>>()
        installingAddonRules = addonRules
        try {
            install()
        } finally {
            installingAddonRules = previousAddonRules
        }
        addons.removeAll { it.id == id }
        addons += InstalledAddon(id = id, rules = addonRules.map { rule -> rule.snapshot() })
    }

    /**
     * Builds an immutable configuration snapshot.
     */
    fun build(): FiktionConfig =
        FiktionConfig(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot() },
            rules = rules.map { rule -> rule.snapshot() },
            metadata = metadata,
        ).normalized()
}
