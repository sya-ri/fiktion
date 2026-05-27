package dev.s7a.fiktion

/**
 * Relative rule specificity within the same precedence layer, ordered from broadest to narrowest match.
 */
internal enum class RuleSpecificity {
    /**
     * Matches only by generated value type.
     */
    TYPE,

    /**
     * Matches by property name, optionally constrained by value type.
     */
    NAME,

    /**
     * Matches by owner type and generated value type.
     */
    OWNER_TYPE,

    /**
     * Matches by owner type, property name, and generated value type.
     */
    OWNER_NAME,

    /**
     * Matches by the full property path.
     */
    PATH,
}
