package dev.s7a.fiktion.metadata

/**
 * Generated metadata for a property that Fiktion can inspect.
 */
public data class FiktionPropertyMetadata(
    /**
     * Type that owns this property.
     */
    public val owner: FiktionTypeRef,
    /**
     * Property name.
     */
    public val name: String,
    /**
     * Property type.
     */
    public val type: FiktionTypeRef,
    /**
     * Property nullability.
     */
    public val nullability: FiktionNullability,
    /**
     * Whether this property has a default value in the selected constructor.
     */
    public val hasDefault: Boolean,
)
