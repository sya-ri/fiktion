package dev.s7a.fiktion

/**
 * Runtime nullability information for a generated property.
 */
public enum class FakeNullability {
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
