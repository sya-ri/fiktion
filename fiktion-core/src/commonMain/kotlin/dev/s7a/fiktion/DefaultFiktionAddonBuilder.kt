package dev.s7a.fiktion

/**
 * Internal mutable implementation for add-on rule builders.
 */
internal class DefaultFiktionAddonBuilder(
    /**
     * Mutable configuration receiving add-on rules.
     */
    override val mutableConfig: MutableFiktionConfig,
) : FiktionAddonBuilder()
