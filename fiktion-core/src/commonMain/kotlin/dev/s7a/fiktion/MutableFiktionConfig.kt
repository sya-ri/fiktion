@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Mutable builder-side representation of [FiktionConfigState].
 */
internal class MutableFiktionConfig(
    seed: Long? = null,
    addons: List<InstalledAddon> = emptyList(),
    rules: List<DefaultGenerationSpec<*>> = emptyList(),
    configs: List<DefaultConfigSpec<*>> = emptyList(),
    collectionConverters: List<CollectionConverter> = emptyList(),
    mapConverters: List<MapConverter> = emptyList(),
    metadata: Map<String, FiktionTypeMetadata<*>> = emptyMap(),
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
    private val rules: MutableList<DefaultGenerationSpec<*>> = rules.toMutableList()

    /**
     * Explicit generator config buffer.
     */
    private val configs: MutableList<DefaultConfigSpec<*>> = configs.toMutableList()

    /**
     * Explicit collection converter buffer.
     */
    private val collectionConverters: MutableList<CollectionConverter> = collectionConverters.toMutableList()

    /**
     * Explicit map converter buffer.
     */
    private val mapConverters: MutableList<MapConverter> = mapConverters.toMutableList()

    /**
     * Type construction metadata keyed by stable type id.
     */
    private val metadata: MutableMap<String, FiktionTypeMetadata<*>> = metadata.toMutableMap()

    /**
     * Rule buffer for the add-on currently being installed.
     */
    private var installingAddonRules: MutableList<DefaultGenerationSpec<*>>? = null

    /**
     * Config buffer for the add-on currently being installed.
     */
    private var installingAddonConfigs: MutableList<DefaultConfigSpec<*>>? = null

    /**
     * Collection converter buffer for the add-on currently being installed.
     */
    private var installingAddonCollectionConverters: MutableList<CollectionConverter>? = null

    /**
     * Map converter buffer for the add-on currently being installed.
     */
    private var installingAddonMapConverters: MutableList<MapConverter>? = null

    /**
     * Adds [rule], replacing an existing rule with the same key within the current rule layer.
     */
    fun add(rule: DefaultGenerationSpec<*>) {
        val targetRules = installingAddonRules ?: rules
        targetRules.removeAll { it.key == rule.key }
        targetRules += rule
    }

    /**
     * Adds [config], replacing an existing config with the same key and matcher within the current layer.
     */
    fun add(config: DefaultConfigSpec<*>) {
        val targetConfigs = installingAddonConfigs ?: configs
        targetConfigs.removeAll { it.key == config.key && it.matcher == config.matcher }
        targetConfigs += config
    }

    /**
     * Adds [converter], replacing an existing collection converter with the same key within the current layer.
     */
    fun add(converter: CollectionConverter) {
        val targetConverters = installingAddonCollectionConverters ?: collectionConverters
        targetConverters.removeAll { it.classifier == converter.classifier }
        targetConverters += converter
    }

    /**
     * Adds [converter], replacing an existing map converter with the same key within the current layer.
     */
    fun add(converter: MapConverter) {
        val targetConverters = installingAddonMapConverters ?: mapConverters
        targetConverters.removeAll { it.classifier == converter.classifier }
        targetConverters += converter
    }

    /**
     * Registers [metadata], replacing existing metadata for the same generated type.
     */
    fun register(metadata: FiktionTypeMetadata<*>) {
        this.metadata[metadata.type.nonNullTypeId()] = metadata
    }

    /**
     * Runs [install] while collecting rules for the add-on identified by [id].
     */
    fun installAddon(
        id: String,
        install: () -> Unit,
    ) {
        val previousAddonRules = installingAddonRules
        val previousAddonConfigs = installingAddonConfigs
        val previousAddonCollectionConverters = installingAddonCollectionConverters
        val previousAddonMapConverters = installingAddonMapConverters
        val addonRules = mutableListOf<DefaultGenerationSpec<*>>()
        val addonConfigs = mutableListOf<DefaultConfigSpec<*>>()
        val addonCollectionConverters = mutableListOf<CollectionConverter>()
        val addonMapConverters = mutableListOf<MapConverter>()
        installingAddonRules = addonRules
        installingAddonConfigs = addonConfigs
        installingAddonCollectionConverters = addonCollectionConverters
        installingAddonMapConverters = addonMapConverters
        try {
            install()
        } finally {
            installingAddonRules = previousAddonRules
            installingAddonConfigs = previousAddonConfigs
            installingAddonCollectionConverters = previousAddonCollectionConverters
            installingAddonMapConverters = previousAddonMapConverters
        }
        addons.removeAll { it.id == id }
        addons +=
            InstalledAddon(
                id = id,
                rules = addonRules.map { rule -> rule.snapshot() },
                configs = addonConfigs.map { config -> config.snapshot() },
                collectionConverters = addonCollectionConverters,
                mapConverters = addonMapConverters,
            )
    }

    /**
     * Builds an immutable configuration snapshot.
     */
    fun build(): FiktionConfigState =
        FiktionConfigState(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot() },
            rules = rules.map { rule -> rule.snapshot() },
            configs = configs.map { config -> config.snapshot() },
            collectionConverters = collectionConverters,
            mapConverters = mapConverters,
            metadata = metadata,
        ).normalized()
}
