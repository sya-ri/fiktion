package dev.s7a.fiktion

import dev.s7a.fiktion.runtime.ExperimentalFiktionApi

/**
 * Builder exposed when creating isolated Fiktion instances.
 */
public sealed interface FiktionBuilder : FiktionRuleBuilder {
    /**
     * Sets the root seed used by generated values.
     */
    public infix fun withSeed(seed: Long)

    /**
     * Installs reusable rules from [addon].
     */
    public fun install(addon: FiktionAddon)

    /**
     * Registers type construction [metadata].
     */
    @ExperimentalFiktionApi
    public fun <T> register(metadata: FiktionTypeMetadata<T>)
}
