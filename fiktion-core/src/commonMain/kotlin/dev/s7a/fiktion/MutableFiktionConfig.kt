@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Mutable builder-side representation of [FiktionConfig].
 */
internal class MutableFiktionConfig(
    seed: Long? = null,
    addons: List<InstalledAddon> = emptyList(),
    rules: List<DefaultGenerationSpec<*>> = emptyList(),
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
     * Mutable map generation specs keyed by the same rule identity used for replacement.
     */
    private val mapGenerationSpecs: MutableMap<RuleKey, DefaultMapGenerationSpec<*, *, *>> = mutableMapOf()

    /**
     * Rule buffer for the add-on currently being installed.
     */
    private var installingAddonRules: MutableList<DefaultGenerationSpec<*>>? = null

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
     * Returns the shared map generation spec for [key].
     */
    @Suppress("UNCHECKED_CAST")
    fun <Key, Value, MapType : Map<Key, Value>> mapGenerationSpec(
        key: RuleKey,
        create: () -> DefaultMapGenerationSpec<Key, Value, MapType>,
    ): DefaultMapGenerationSpec<Key, Value, MapType> =
        mapGenerationSpecs.getOrPut(key) { create() } as DefaultMapGenerationSpec<Key, Value, MapType>

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
        val previousAddonCollectionConverters = installingAddonCollectionConverters
        val previousAddonMapConverters = installingAddonMapConverters
        val addonRules = mutableListOf<DefaultGenerationSpec<*>>()
        val addonCollectionConverters = mutableListOf<CollectionConverter>()
        val addonMapConverters = mutableListOf<MapConverter>()
        installingAddonRules = addonRules
        installingAddonCollectionConverters = addonCollectionConverters
        installingAddonMapConverters = addonMapConverters
        try {
            install()
        } finally {
            installingAddonRules = previousAddonRules
            installingAddonCollectionConverters = previousAddonCollectionConverters
            installingAddonMapConverters = previousAddonMapConverters
        }
        addons.removeAll { it.id == id }
        addons +=
            InstalledAddon(
                id = id,
                rules = addonRules.map { rule -> rule.snapshot() },
                collectionConverters = addonCollectionConverters,
                mapConverters = addonMapConverters,
            )
    }

    /**
     * Builds an immutable configuration snapshot.
     */
    fun build(): FiktionConfig =
        FiktionConfig(
            seed = seed,
            addons = addons.map { addon -> addon.snapshot() },
            rules = rules.map { rule -> rule.snapshot() },
            collectionConverters = collectionConverters,
            mapConverters = mapConverters,
            metadata = metadata,
        ).normalized()
}
