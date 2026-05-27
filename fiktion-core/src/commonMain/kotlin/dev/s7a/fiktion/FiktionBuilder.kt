package dev.s7a.fiktion

/**
 * Builder exposed when creating isolated Fiktion instances.
 */
public interface FiktionBuilder : FiktionRuleBuilder {
    /**
     * Sets the root seed used by generated values.
     */
    public infix fun withSeed(seed: Long)

    /**
     * Installs reusable rules from [addon].
     */
    public fun install(addon: FiktionAddon)
}
