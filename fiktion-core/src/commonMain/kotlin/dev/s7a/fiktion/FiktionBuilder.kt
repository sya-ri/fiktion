package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi

/**
 * Builder exposed when creating isolated Fiktion instances.
 */
public sealed class FiktionBuilder protected constructor() : FiktionRuleBuilder() {
    /**
     * Sets the root seed used by generated values.
     */
    public abstract infix fun withSeed(seed: Long)

    /**
     * Installs reusable rules from [addon].
     */
    public abstract fun install(addon: FiktionAddon)

    /**
     * Registers type construction [metadata].
     */
    @ExperimentalFiktionApi
    public abstract fun <T> register(metadata: FiktionTypeMetadata<T>)
}
