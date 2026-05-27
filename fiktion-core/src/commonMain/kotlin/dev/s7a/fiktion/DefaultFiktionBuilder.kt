@file:OptIn(dev.s7a.fiktion.runtime.ExperimentalFiktionApi::class)

package dev.s7a.fiktion

/**
 * Internal mutable implementation for isolated and global Fiktion configuration builders.
 */
internal class DefaultFiktionBuilder private constructor(
    /**
     * Mutable configuration being built.
     */
    config: MutableFiktionConfig,
) : DefaultFiktionRuleBuilder(config),
    FiktionConfigureBuilder {
    /**
     * Creates an empty builder.
     */
    constructor() : this(MutableFiktionConfig())

    /**
     * Creates a builder initialized from [config].
     */
    constructor(config: FiktionConfig) :
        this(
            MutableFiktionConfig(
                seed = config.seed,
                addons = config.addons,
                rules = config.rules,
                metadata = config.metadata,
            ),
        )

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

    @dev.s7a.fiktion.runtime.ExperimentalFiktionApi
    override fun <T> register(metadata: FiktionTypeMetadata<T>) {
        config.register(metadata)
    }
}
