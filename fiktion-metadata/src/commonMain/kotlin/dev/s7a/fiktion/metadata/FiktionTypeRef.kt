package dev.s7a.fiktion.metadata

/**
 * Stable reference to a Kotlin type.
 */
public data class FiktionTypeRef(
    /**
     * Stable type identifier.
     */
    public val id: String,
    /**
     * Human-readable type name used in diagnostics.
     */
    public val displayName: String,
    /**
     * Type arguments for this reference.
     */
    public val arguments: List<FiktionTypeRef> = emptyList(),
)
