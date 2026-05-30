@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Internal mutable implementation for isolated and global Fiktion configuration builders.
 */
internal class DefaultFiktionBuilder private constructor(
    /**
     * Mutable configuration being built.
     */
    override val mutableConfig: MutableFiktionConfig,
) : FiktionConfigureBuilder() {
    /**
     * Creates an empty builder.
     */
    constructor(installAutomaticAddons: Boolean = true) : this(MutableFiktionConfig()) {
        if (installAutomaticAddons) {
            installAutomaticAddons()
        }
    }

    /**
     * Creates a builder initialized from [config].
     */
    constructor(
        config: FiktionConfigState,
        installAutomaticAddons: Boolean = true,
    ) :
        this(
            MutableFiktionConfig(
                seed = config.seed,
                addons = config.addons,
                rules = config.rules,
                configs = config.configs,
                collectionConverters = config.collectionConverters,
                mapConverters = config.mapConverters,
                metadata = config.metadata,
            ),
        ) {
        if (installAutomaticAddons) {
            installAutomaticAddons()
        }
    }

    /**
     * Builds an immutable configuration snapshot.
     */
    fun build(): FiktionConfigState = mutableConfig.build()

    override fun withSeed(seed: Long) {
        mutableConfig.seed = seed
    }

    override fun install(addon: FiktionAddon) {
        mutableConfig.installAddon(addon.id) {
            addon.install(DefaultFiktionAddonBuilder(mutableConfig))
        }
    }

    @ExperimentalFiktionApi
    override fun <T> register(metadata: FiktionTypeMetadata<T>) {
        mutableConfig.register(metadata)
    }

    internal fun installAutomaticAddons() {
        automaticAddons().forEach { addon -> install(addon) }
    }
}
