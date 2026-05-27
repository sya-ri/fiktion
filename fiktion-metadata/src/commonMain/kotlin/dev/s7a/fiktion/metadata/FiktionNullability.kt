package dev.s7a.fiktion.metadata

/**
 * Nullability information captured in generated metadata.
 */
public enum class FiktionNullability {
    /**
     * The property accepts `null`.
     */
    NULLABLE,

    /**
     * The property does not accept `null`.
     */
    NON_NULL,

    /**
     * The property nullability could not be determined.
     */
    UNKNOWN,
}
