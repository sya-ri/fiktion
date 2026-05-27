package dev.s7a.fiktion

/**
 * Rule precedence layer, ordered from lowest to highest priority.
 */
internal enum class RulePrecedence {
    /**
     * Rules contributed by installed add-ons.
     */
    ADDON,

    /**
     * Rules configured globally through [Fiktion.configure].
     */
    GLOBAL,

    /**
     * Rules configured on an isolated [Fiktion] instance.
     */
    INSTANCE,

    /**
     * Rules configured for a single fake generation call.
     */
    PER_CALL,
}
