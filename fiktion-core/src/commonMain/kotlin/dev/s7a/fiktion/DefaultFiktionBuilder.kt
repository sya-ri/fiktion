@file:OptIn(ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Internal mutable implementation for isolated and global Fiktion configuration builders.
 */
internal class DefaultFiktionBuilder private constructor(
    /**
     * Mutable configuration being built.
     */
    override val config: MutableFiktionConfig,
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
        config: FiktionConfig,
        installAutomaticAddons: Boolean = true,
    ) :
        this(
            MutableFiktionConfig(
                seed = config.seed,
                addons = config.addons,
                rules = config.rules,
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
    fun build(): FiktionConfig = config.build()

    override fun withSeed(seed: Long) {
        config.seed = seed
    }

    override fun install(addon: FiktionAddon) {
        config.installAddon(addon.id) {
            addon.install(DefaultFiktionAddonBuilder(config))
        }
    }

    @ExperimentalFiktionApi
    override fun <T> register(metadata: FiktionTypeMetadata<T>) {
        config.register(metadata)
    }

    internal fun installAutomaticAddons() {
        automaticAddons().forEach { addon -> install(addon) }
    }
}
